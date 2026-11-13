package org.rsa.Keys;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class GenerateKeys {
    public GenerateKeys() {}

    public boolean keysExist() {
        return Files.exists(Path.of("alunoUm/keys/public_key.pem")) &&
               Files.exists(Path.of("alunoUm/keys/private_key.pem")) &&
               Files.exists(Path.of("alunoDois/keys/public_key.pem")) &&
               Files.exists(Path.of("alunoDois/keys/private_key.pem"));
    }

    public void generateKeys() throws NoSuchAlgorithmException, NoSuchPaddingException, IOException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);

        KeyPair personOneKeys = kpg.genKeyPair();
        KeyPair personTwoKeys = kpg.genKeyPair();

        saveKeys(personOneKeys, personTwoKeys);
    }

    private void saveKeys(KeyPair p1, KeyPair p2) throws IOException {
        Files.createDirectories(Path.of("alunoUm/keys"));
        Files.createDirectories(Path.of("alunoDois/keys"));

        Files.writeString(
                Path.of("alunoUm/keys/public_key.pem"),
                Base64.getMimeEncoder(64, new byte[]{}).encodeToString(p1.getPublic().getEncoded())
        );

        Files.writeString(
                Path.of("alunoUm/keys/private_key.pem"),
                Base64.getMimeEncoder(64, new byte[]{}).encodeToString(p1.getPrivate().getEncoded())
        );

        Files.writeString(
                Path.of("alunoDois/keys/public_key.pem"),
                Base64.getMimeEncoder(64, new byte[]{}).encodeToString(p2.getPublic().getEncoded())
        );

        Files.writeString(
                Path.of("alunoDois/keys/private_key.pem"),
                Base64.getMimeEncoder(64, new byte[]{}).encodeToString(p2.getPrivate().getEncoded())
        );
    }
}
