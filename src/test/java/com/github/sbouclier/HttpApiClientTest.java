package com.github.sbouclier;

import com.github.sbouclier.result.AssetInformationResult;
import com.github.sbouclier.result.AssetPairsResult;
import com.github.sbouclier.result.ServerTimeResult;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * HttpAPIClient test
 *
 * @author Stéphane Bouclier
 */
public class HttpApiClientTest {

    private HttpApiClient client;
    private CloseableHttpClient mockHttpClient;
    private CloseableHttpResponse mockHttpResponse;
    private HttpEntity mockEntity;

    @Before
    public void setUp() {
        mockHttpClient = mock(CloseableHttpClient.class);
        mockHttpResponse = mock(CloseableHttpResponse.class);
        mockEntity = mock(HttpEntity.class);

        client = new HttpApiClient();
    }

    @Test
    public void should_unmarshal_server_time_result() throws IOException {
        String mockResponseBody = "{\"error\":[],\"result\":{\"unixtime\":1498768391,\"rfc1123\":\"Thu, 29 Jun 17 20:33:11 +0000\"}}";

        when(mockHttpClient.execute(any())).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        when(mockHttpResponse.getEntity()).thenReturn(mockEntity);
        when(mockEntity.getContent()).thenReturn(new ByteArrayInputStream(mockResponseBody.getBytes("UTF-8")));

        HttpApiClient<ServerTimeResult> client = new HttpApiClient<>(mockHttpClient);
        ServerTimeResult result =  client.callHttpClient("https://api.kraken.com/0/public/Time", ServerTimeResult.class);

        assertThat(result.getResult(), hasProperty("unixtime", equalTo(1498768391L)));
        assertThat(result.getResult(), hasProperty("rfc1123", equalTo("Thu, 29 Jun 17 20:33:11 +0000")));
    }

    @Test
    public void should_unmarshal_asset_information_result() throws IOException {
        StringBuilder mockResponseBody = new StringBuilder("{\"error\":[],\"result\":{");
        mockResponseBody.append("\"XETC\":{\"aclass\":\"currency\",\"altname\":\"ETC\",\"decimals\":10,\"display_decimals\":5},");
        mockResponseBody.append("\"XETH\":{\"aclass\":\"currency\",\"altname\":\"ETH\",\"decimals\":10,\"display_decimals\":5},");
        mockResponseBody.append("\"ZEUR\":{\"aclass\":\"currency\",\"altname\":\"EUR\",\"decimals\":4,\"display_decimals\":2},");
        mockResponseBody.append("\"ZUSD\":{\"aclass\":\"currency\",\"altname\":\"USD\",\"decimals\":4,\"display_decimals\":2}");
        mockResponseBody.append("}}");

        AssetInformationResult.AssetInformation xetc = new AssetInformationResult.AssetInformation();
        xetc.setAlternateName("ETC");
        xetc.setAssetClass("currency");
        xetc.setDecimals((byte) 10);
        xetc.setDisplayDecimals((byte) 5);

        AssetInformationResult.AssetInformation xeth = new AssetInformationResult.AssetInformation();
        xeth.setAlternateName("ETH");
        xeth.setAssetClass("currency");
        xeth.setDecimals((byte) 10);
        xeth.setDisplayDecimals((byte) 5);

        AssetInformationResult.AssetInformation zeur = new AssetInformationResult.AssetInformation();
        zeur.setAlternateName("EUR");
        zeur.setAssetClass("currency");
        zeur.setDecimals((byte) 4);
        zeur.setDisplayDecimals((byte) 2);

        AssetInformationResult.AssetInformation zusd = new AssetInformationResult.AssetInformation();
        zusd.setAlternateName("USD");
        zusd.setAssetClass("currency");
        zusd.setDecimals((byte) 4);
        zusd.setDisplayDecimals((byte) 2);

        when(mockHttpClient.execute(any())).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        when(mockHttpResponse.getEntity()).thenReturn(mockEntity);
        when(mockEntity.getContent()).thenReturn(new ByteArrayInputStream(mockResponseBody.toString().getBytes("UTF-8")));

        HttpApiClient<AssetInformationResult> client = new HttpApiClient<>(mockHttpClient);
        AssetInformationResult result =  client.callHttpClient("https://api.kraken.com/0/public/Assets", AssetInformationResult.class);

        assertEquals(result.getResult().size(), 4);
        assertThat(result.getResult().get("XETC"), samePropertyValuesAs(xetc));
        assertThat(result.getResult().get("XETH"), samePropertyValuesAs(xeth));
        assertThat(result.getResult().get("ZEUR"), samePropertyValuesAs(zeur));
        assertThat(result.getResult().get("ZUSD"), samePropertyValuesAs(zusd));
    }

    @Test
    public void should_unmarshal_asset_pairs_result() throws IOException {
        StringBuilder mockResponseBody = new StringBuilder("{\"error\":[],\"result\":{");
        mockResponseBody.append("\"XETCXXBT\":{\"altname\":\"ETCXBT\",\"aclass_base\":\"currency\",\"base\":\"XETC\",\"aclass_quote\":\"currency\",\"quote\":\"XXBT\",\"lot\":\"unit\",\"pair_decimals\":8,\"lot_decimals\":8,\"lot_multiplier\":1,\"leverage_buy\":[2,3],\"leverage_sell\":[2,3],\"fees\":[[0,0.26],[50000,0.24],[100000,0.22],[250000,0.2],[500000,0.18],[1000000,0.16],[2500000,0.14],[5000000,0.12],[10000000,0.1]],\"fees_maker\":[[0,0.16],[50000,0.14],[100000,0.12],[250000,0.1],[500000,0.08],[1000000,0.06],[2500000,0.04],[5000000,0.02],[10000000,0]],\"fee_volume_currency\":\"ZUSD\",\"margin_call\":80,\"margin_stop\":40},");
        mockResponseBody.append("\"XETCZEUR\":{\"altname\":\"ETCEUR\",\"aclass_base\":\"currency\",\"base\":\"XETC\",\"aclass_quote\":\"currency\",\"quote\":\"ZEUR\",\"lot\":\"unit\",\"pair_decimals\":5,\"lot_decimals\":8,\"lot_multiplier\":1,\"leverage_buy\":[2],\"leverage_sell\":[2],\"fees\":[[0,0.26],[50000,0.24],[100000,0.22],[250000,0.2],[500000,0.18],[1000000,0.16],[2500000,0.14],[5000000,0.12],[10000000,0.1]],\"fees_maker\":[[0,0.16],[50000,0.14],[100000,0.12],[250000,0.1],[500000,0.08],[1000000,0.06],[2500000,0.04],[5000000,0.02],[10000000,0]],\"fee_volume_currency\":\"ZUSD\",\"margin_call\":80,\"margin_stop\":40},");
        mockResponseBody.append("\"XETCZUSD\":{\"altname\":\"ETCUSD\",\"aclass_base\":\"currency\",\"base\":\"XETC\",\"aclass_quote\":\"currency\",\"quote\":\"ZUSD\",\"lot\":\"unit\",\"pair_decimals\":5,\"lot_decimals\":8,\"lot_multiplier\":1,\"leverage_buy\":[2],\"leverage_sell\":[2],\"fees\":[[0,0.26],[50000,0.24],[100000,0.22],[250000,0.2],[500000,0.18],[1000000,0.16],[2500000,0.14],[5000000,0.12],[10000000,0.1]],\"fees_maker\":[[0,0.16],[50000,0.14],[100000,0.12],[250000,0.1],[500000,0.08],[1000000,0.06],[2500000,0.04],[5000000,0.02],[10000000,0]],\"fee_volume_currency\":\"ZUSD\",\"margin_call\":80,\"margin_stop\":40},");
        mockResponseBody.append("\"XETHZEUR\":{\"altname\":\"ETHEUR\",\"aclass_base\":\"currency\",\"base\":\"XETH\",\"aclass_quote\":\"currency\",\"quote\":\"ZEUR\",\"lot\":\"unit\",\"pair_decimals\":5,\"lot_decimals\":8,\"lot_multiplier\":1,\"leverage_buy\":[2,3,4,5],\"leverage_sell\":[2,3,4,5],\"fees\":[[0,0.26],[50000,0.24],[100000,0.22],[250000,0.2],[500000,0.18],[1000000,0.16],[2500000,0.14],[5000000,0.12],[10000000,0.1]],\"fees_maker\":[[0,0.16],[50000,0.14],[100000,0.12],[250000,0.1],[500000,0.08],[1000000,0.06],[2500000,0.04],[5000000,0.02],[10000000,0]],\"fee_volume_currency\":\"ZUSD\",\"margin_call\":80,\"margin_stop\":40},");
        mockResponseBody.append("\"XZECZUSD\":{\"altname\":\"ZECUSD\",\"aclass_base\":\"currency\",\"base\":\"XZEC\",\"aclass_quote\":\"currency\",\"quote\":\"ZUSD\",\"lot\":\"unit\",\"pair_decimals\":5,\"lot_decimals\":8,\"lot_multiplier\":1,\"leverage_buy\":[],\"leverage_sell\":[],\"fees\":[[0,0.26],[50000,0.24],[100000,0.22],[250000,0.2],[500000,0.18],[1000000,0.16],[2500000,0.14],[5000000,0.12],[10000000,0.1]],\"fees_maker\":[[0,0.16],[50000,0.14],[100000,0.12],[250000,0.1],[500000,0.08],[1000000,0.06],[2500000,0.04],[5000000,0.02],[10000000,0]],\"fee_volume_currency\":\"ZUSD\",\"margin_call\":80,\"margin_stop\":40}");
        mockResponseBody.append("}}");

        when(mockHttpClient.execute(any())).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        when(mockHttpResponse.getEntity()).thenReturn(mockEntity);
        when(mockEntity.getContent()).thenReturn(new ByteArrayInputStream(mockResponseBody.toString().getBytes("UTF-8")));

        HttpApiClient<AssetPairsResult> client = new HttpApiClient<>(mockHttpClient);
        AssetPairsResult result =  client.callHttpClient("https://api.kraken.com/0/public/AssetPairs", AssetPairsResult.class);
        AssetPairsResult.AssetPair pair = result.getResult().get("XETCXXBT");

        assertEquals(result.getResult().size(), 5);

        assertEquals("ETCXBT", pair.getAlternatePairName());
        assertEquals("currency", pair.getBaseAssetClass());
        assertEquals("XETC", pair.getBaseAssetId());
        assertEquals("currency", pair.getQuoteAssetClass());
        assertEquals("XXBT", pair.getQuoteAssetId());
        assertEquals("unit", pair.getLot());

        assertEquals(8, pair.getPairDecimals().intValue());
        assertEquals(8, pair.getLotDecimals().intValue());
        assertEquals(1, pair.getLotMultiplier().intValue());

        assertThat(pair.getLeverageBuy(), contains(2,3));
        assertThat(pair.getLeverageSell(), contains(2,3));

        assertThat(pair.getFees(), contains(
                new AssetPairsResult.AssetPair.Fee(0, 0.26f),
                new AssetPairsResult.AssetPair.Fee(50000, 0.24f),
                new AssetPairsResult.AssetPair.Fee(100000, 0.22f),
                new AssetPairsResult.AssetPair.Fee(250000, 0.2f),
                new AssetPairsResult.AssetPair.Fee(500000, 0.18f),
                new AssetPairsResult.AssetPair.Fee(1000000, 0.16f),
                new AssetPairsResult.AssetPair.Fee(2500000, 0.14f),
                new AssetPairsResult.AssetPair.Fee(5000000, 0.12f),
                new AssetPairsResult.AssetPair.Fee(10000000, 0.1f)
        ));

        assertThat(pair.getFeesMaker(), contains(
                new AssetPairsResult.AssetPair.Fee(0, 0.16f),
                new AssetPairsResult.AssetPair.Fee(50000, 0.14f),
                new AssetPairsResult.AssetPair.Fee(100000, 0.12f),
                new AssetPairsResult.AssetPair.Fee(250000, 0.1f),
                new AssetPairsResult.AssetPair.Fee(500000, 0.08f),
                new AssetPairsResult.AssetPair.Fee(1000000, 0.06f),
                new AssetPairsResult.AssetPair.Fee(2500000, 0.04f),
                new AssetPairsResult.AssetPair.Fee(5000000, 0.02f),
                new AssetPairsResult.AssetPair.Fee(10000000, 0f)
        ));

        assertEquals(80,pair.getMarginCall().intValue());
        assertEquals(40,pair.getMarginStop().intValue());
    }
}
