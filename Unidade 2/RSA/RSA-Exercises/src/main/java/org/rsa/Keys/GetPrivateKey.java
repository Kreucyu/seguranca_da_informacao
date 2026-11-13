package org.rsa.Keys;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class GetPrivateKey {
    public GetPrivateKey() {}

    public PrivateKey getPrivateKey(int person) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
            byte[] keyBytes = Files.readAllBytes(Path.of(
                    person == 1
                            ? "alunoUm/keys/private_key.pem"
                            : "alunoDois/keys/private_key.pem"
            ));

            byte[] decodedKey = Base64.getMimeDecoder()
                    .decode(keyBytes);

            PKCS8EncodedKeySpec spec =
                    new PKCS8EncodedKeySpec(decodedKey);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePrivate(spec);
    }
}
