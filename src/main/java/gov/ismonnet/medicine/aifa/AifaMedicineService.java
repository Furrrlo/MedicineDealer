package gov.ismonnet.medicine.aifa;

import gov.ismonnet.medicine.jaxb.ws.Medicina;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AifaMedicineService implements MedicineService {

    private static final String AIFA_API = "https://www.agenziafarmaco.gov.it/services/";

    @Override
    public Medicina getMedicineByAic(String aic) {
        try {
            final JSONArray medicine = select(
                    Arrays.asList(AifaField.CODICE_FARMACO, AifaField.DESCRIZIONE_FARMACO),
                    AifaQuery.builder()
                            .param(AifaField.BUNDLE).eq("confezione_farmaco")
                            .param(AifaField.CODICE_FARMACO).eq(aic)
                            .build(),
                    AifaField.CODICE_FARMACO)
                    .getJSONObject("response")
                    .getJSONArray("docs");

            final List<Medicina> medicines = jsonToMedicines(medicine);
            if(medicines.isEmpty())
                return null;
            return medicines.get(0);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<Medicina> findMedicinesByAic(String aic) {
        try {
            final JSONArray medicines = select(
                    Arrays.asList(AifaField.CODICE_FARMACO, AifaField.DESCRIZIONE_FARMACO),
                    AifaQuery.builder()
                            .param(AifaField.BUNDLE).eq("confezione_farmaco")
                            .param(AifaField.CODICE_FARMACO).startsWith(aic)
                            .build(),
                    AifaField.CODICE_FARMACO)
                    .getJSONObject("response")
                    .getJSONArray("docs");

            return jsonToMedicines(medicines);

        } catch (NullPointerException e) {
            throw new UncheckedIOException(new IOException("Couldn't parse response", e));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<Medicina> findMedicinesByName(String name) {
        try {
            final JSONArray medicines = select(
                    Arrays.asList(AifaField.CODICE_FARMACO, AifaField.DESCRIZIONE_FARMACO),
                    AifaQuery.builder()
                            .param(AifaField.BUNDLE).eq("confezione_farmaco")
                            .param(AifaField.DESCRIZIONE_FARMACO).startsWith(name)
                            .build(),
                    AifaField.DESCRIZIONE_FARMACO)
                    .getJSONObject("response")
                    .getJSONArray("docs");

            return jsonToMedicines(medicines);
        } catch (NullPointerException e) {
            throw new UncheckedIOException(new IOException("Couldn't parse response", e));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private List<Medicina> jsonToMedicines(JSONArray medicines) {
        return new ArrayList<>(StreamSupport.stream(medicines.spliterator(), false)
                .filter(o -> o instanceof JSONObject)
                .map(JSONObject.class::cast)
                .filter(o -> o.getJSONArray("sm_field_codice_farmaco") != null)
                .filter(o -> !o.getJSONArray("sm_field_codice_farmaco").isEmpty())
                .filter(o -> o.getJSONArray("sm_field_descrizione_farmaco") != null)
                .filter(o -> !o.getJSONArray("sm_field_descrizione_farmaco").isEmpty())
                .map(o -> new Medicina(
                        o.getJSONArray("sm_field_descrizione_farmaco").getString(0),
                        o.getJSONArray("sm_field_codice_farmaco").getString(0)
                ))
                .collect(Collectors.toMap(Medicina::getName, p -> p, (p, q) -> p))
                .values());
    }

    public static JSONObject select(final AifaQuery query,
                                    final AifaField df) throws IOException {
        return select(Collections.emptyList(), query, df);
    }

    public static JSONObject select(final Collection<AifaField> fields,
                                    final AifaQuery query,
                                    final AifaField df) throws IOException {
        return select(fields, query, df, null, null);
    }

    public static JSONObject select(AifaField[] fields,
                                    final AifaQuery query,
                                    final AifaField df) throws IOException {
        return select(fields, query, df, null, null);
    }

    public static JSONObject select(AifaField[] fields,
                                    final AifaQuery query,
                                final AifaField df,
                                final String format,
                                final Integer rows) throws IOException {
        return select(Arrays.asList(fields), query, df, format, rows);
    }

    public static JSONObject select(final Collection<AifaField> fields,
                                    final AifaQuery query,
                                    final AifaField df,
                                    final String format,
                                    final Integer rows) throws IOException {

        final URL url0 = new URL(AIFA_API + "search/select?" + makeQuery(fields, query, df, format, rows));
        final HttpURLConnection connection = (HttpURLConnection) url0.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.connect();

        if(connection.getResponseCode() != 200)
            throw new IOException(connection.getResponseMessage() != null ?
                    connection.getResponseMessage() :
                    String.valueOf(connection.getResponseCode()));

        final String jsonString;
        try(final BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {

            final StringJoiner sj = new StringJoiner("\n");
            String line;
            while((line = br.readLine()) != null)
                sj.add(line);
            jsonString = sj.toString();
        }

        return new JSONObject(jsonString);
    }

    private static String makeQuery(final Collection<AifaField> fields,
                                    final AifaQuery query,
                                    final AifaField df,
                                    final String format,
                                    final Integer rows) {

        String queryString = "";
        if(fields != null && !fields.isEmpty())
            queryString += "&fl=" + fields.stream()
                    .map(AifaField::getName)
                    .collect(Collectors.joining(","));
        if(query != null)
            queryString += "&q=" + query.getQueryString();
        if(df != null)
            queryString += "&df=" + df.getName();
        queryString += "&wt=" + (format == null ? "json" : format);
        queryString += "&rows=" + (rows == null ? 150000 : rows);
        return queryString.substring(1);
    }
}
