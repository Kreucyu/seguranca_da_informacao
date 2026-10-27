package com.exemplo.hmacapi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.exemplo.hmacapi.config.HmacProperties;
import com.exemplo.hmacapi.security.HmacSigner;

@SpringBootTest
@AutoConfigureMockMvc
class HmacAuthIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired HmacProperties props;

    private ResultActions call(String consumer, String method, String path, long ts,
                               String signedBody, String sentBody) throws Exception {
        String secret = props.getConsumers().get(consumer);
        byte[] toSign = signedBody == null ? new byte[0] : signedBody.getBytes(StandardCharsets.UTF_8);
        String sig = HmacSigner.sign(secret, method, path, String.valueOf(ts), consumer, toSign);
        MockHttpServletRequestBuilder req = request(HttpMethod.valueOf(method), path)
                .header("X-Consumer-Id", consumer)
                .header("X-Timestamp", String.valueOf(ts))
                .header("X-Signature", sig);
        if (sentBody != null) {
            req.contentType(MediaType.APPLICATION_JSON).content(sentBody.getBytes(StandardCharsets.UTF_8));
        }
        return mvc.perform(req);
    }

    private static long now() { return Instant.now().getEpochSecond(); }

    @Test
    void doisConsumidoresComSucesso() throws Exception {
        String body = "{\"nome\":\"Teclado\",\"preco\":99.90,\"quantidade\":3}";
        call("consumidor-a", "POST", "/api/produtos", now(), body, body).andExpect(status().isCreated());
        call("consumidor-b", "GET", "/api/produtos", now(), null, null).andExpect(status().isOk());
    }

    @Test
    void corpoAdulteradoRetorna401() throws Exception {
        String assinado = "{\"nome\":\"Mouse\",\"preco\":50.00,\"quantidade\":1}";
        String enviado = "{\"nome\":\"Mouse\",\"preco\":0.01,\"quantidade\":1}";
        call("consumidor-a", "POST", "/api/produtos", now(), assinado, enviado).andExpect(status().isUnauthorized());
    }

    @Test
    void timestampAntigoRetorna401() throws Exception {
        String body = "{\"nome\":\"Monitor\",\"preco\":800.00,\"quantidade\":2}";
        call("consumidor-a", "POST", "/api/produtos", now() - 300, body, body).andExpect(status().isUnauthorized());
    }

    @Test
    void replayDentroDaJanelaRetorna401() throws Exception {
        long ts = now();
        call("consumidor-b", "GET", "/api/produtos?replay=1", ts, null, null).andExpect(status().isOk());
        call("consumidor-b", "GET", "/api/produtos?replay=1", ts, null, null).andExpect(status().isUnauthorized());
    }

    @Test
    void semCabecalhosRetorna401() throws Exception {
        mvc.perform(request(HttpMethod.GET, "/api/produtos")).andExpect(status().isUnauthorized());
    }
}
