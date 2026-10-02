from fastapi import FastAPI, Depends, HTTPException, status, Request
from sqlalchemy import Column, Integer, String, create_engine
from sqlalchemy.orm import sessionmaker, Session, declarative_base
from pydantic import BaseModel
import uvicorn
import os

DATABASE_URL = "sqlite:///./users.db"
engine = create_engine(DATABASE_URL, connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

class User(Base):
    __tablename__ = "users"
    id = Column(Integer, primary_key=True, index=True)
    username = Column(String, unique=True, index=True)
    password = Column(String)
    email = Column(String)

def getAuthCode():
    import random
    return f"{random.randint(0, 9999):4d}"

Base.metadata.create_all(bind=engine)

app = FastAPI()

error_counts: dict[str, int] = {}

auth_error_counts: dict[str, int] = {}

timestamp_block: dict[str, float] = {}

request_delay: dict[str, int] = {}

request_user_ip: dict[str, int] = {}

auth_codes: dict[str, str] = {}

block_duration = 5 * 60

class RegisterRequest(BaseModel):
    username: str
    email: str
    password: str

class LoginRequest(BaseModel):
    username: str
    password: str

class AuthRequest(BaseModel):
    auth_code: str
    email: str

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# Rota para registrar usuário
@app.post("/register/")
def register_user(request: RegisterRequest, db: Session = Depends(get_db)):
    existing_user = db.query(User).filter(User.username == request.username).first()
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Usuário já existe"
        )

    user = User(
        username=request.username,
        email=request.email,
        password=request.password
    )
    db.add(user)
    db.commit()
    db.refresh(user)

    return {"message": "Usuário registrado com sucesso!"}

# Rota de login
@app.post("/login/")
def login(request: LoginRequest, http_request: Request, db: Session = Depends(get_db)):
    user = db.query(User).filter(
        User.username == request.username,
        User.password == request.password
    ).first()

    if http_request.client.host in request_user_ip and request_user_ip[http_request.client.host] > 5:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Endereço IP bloqueado devido a múltiplas tentativas de login falhas"
        )

    if request.username in request_delay:
        import time
        print(f"Delay aplicado para o usuário {request.username}: {request_delay[request.username]} segundos")
        time.sleep(request_delay[request.username])

    if request.username in timestamp_block:
        import time
        now = time.perf_counter()
        if now - timestamp_block[request.username] < block_duration:
            request_delay[request.username] = request_delay.get(request.username, 0) * 2 or 2
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Conta bloqueada devido a múltiplas tentativas de login falhas, tente novamente mais tarde"
            )
        else:
            timestamp_block.pop(request.username, None)
            error_counts[request.username] = 0

    if request.username in error_counts and error_counts[request.username] >= 3:
        if request.username not in timestamp_block:
            import time
            start = time.perf_counter()
            timestamp_block[request.username] = start

        request_delay[request.username] = request_delay.get(request.username, 0) * 2 or 2
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Conta bloqueada devido a múltiplas tentativas de login falhas"
        )

    if not user or user.password != request.password:
        request_delay[request.username] = request_delay.get(request.username, 0) * 2 or 2
        error_counts[request.username] = error_counts.get(request.username, 0) + 1
        request_user_ip[http_request.client.host] = request_user_ip.get(http_request.client.host, 0) + 1

        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Credenciais inválidas"
        )

    request_delay.pop(request.username, None)
    error_counts.pop(request.username, None)
    return sendAuthEmail(user.email);

def sendAuthEmail(email: str):
    import smtplib
    from email.message import EmailMessage
    message = EmailMessage()
    message["Subject"] = "Código de autenticação"
    message["From"] = os.environ["GMAIL_USER"]
    message["To"] = email
    code = getAuthCode()
    auth_codes[email] = code
    auth_error_counts.pop(email, None)  

    message.set_content(
        f"Seu código de autenticação é: {code}, para prosseguir envie o código em '/auth-validator'"
    )

    with smtplib.SMTP_SSL("smtp.gmail.com", 465) as smtp:
        smtp.login(os.environ["GMAIL_USER"], os.environ["GMAIL_APP_PASSWORD"])
        smtp.send_message(message)
    
    return "O código de autentição enviado para o e-mail cadastrado"

@app.post("/auth-validator/")
def validate_code(request: AuthRequest):
    if request.email in auth_error_counts:
        if auth_error_counts[request.email] >= 5:
            raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Número de máximo de tentativas excedidas, tente novamente com outra solicitação"
        )

    if request.email not in auth_codes:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Nenhum código de autenticação encontrado"
        )

    if request.auth_code == auth_codes[request.email]:
        auth_codes.pop(request.email, None)
        auth_error_counts.pop(request.email, None)
        return {"message": "Login realizado com sucesso!"}
    else: 
        auth_error_counts[request.email] = auth_error_counts.get(request.email, 0) + 1;
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Código de autenticação inválido"
        )

# Inicialização direta do servidor
if __name__ == "__main__":
    uvicorn.run(
        "servico:app",
        host="0.0.0.0",
        port=8000,
        reload=False
    )