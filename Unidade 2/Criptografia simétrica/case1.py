from Crypto.Cipher import Blowfish
from Crypto.Util.Padding import pad

def show_value(initial_text, message):
    text_str = "".join([chr(b) if 32 <= b <= 126 else '.' for b in initial_text])
    print(f"{message} (Texto): {text_str}")
    print(f"{message} (Bytes): {list(initial_text)}")

def show_hex_values(text):
    actual_position = 0
    print("\nConteúdo do texto cifrado:\n")
    for b in text:
        if actual_position >= 8:
            print()
            actual_position = 0
            
        signed_b = b if b < 128 else b - 256
        print(f"{signed_b}", end=" ")
        actual_position += 1
        
    actual_position = 0
    print("\n")
    print("\nConteúdo do texto cifrado (hexadecimal):\n")
    for b in text:
        if actual_position >= 8:
            print()
            actual_position = 0
            
        print(f"{b:02X}", end=" ")
        actual_position += 1
    print()

def execute_encrypt():
    print("\n=================| CASO 1 (Python) |=================")
    
    key_values = bytes([65, 66, 67, 68, 69])
    initial_text = b"FURB"
    
    show_value(initial_text, "Valor a ser criptografado")
    print(f"\nChave a ser utilizada: {list(key_values)}")
    
    cipher = Blowfish.new(key_values, Blowfish.MODE_ECB)
    
    padded_text = pad(initial_text, Blowfish.block_size)
    encrypted_text = cipher.encrypt(padded_text)
    
    show_hex_values(encrypted_text)
    print(f"\nTamanho do texto cifrado: {len(encrypted_text)}")

if __name__ == "__main__":
    execute_encrypt()