package org.rsa.Keys;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class GetPublicKey {
    public GetPublicKey() {}

    public PublicKey getPublicKey(int person) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Path.of(
                person == 1
                        ? "alunoUm/keys/public_key.pem"
                        : "alunoDois/keys/public_key.pem"
        ));

        byte[] decodedKey = Base64.getMimeDecoder()
                .decode(keyBytes);

        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(decodedKey);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(spec);
    }
}
