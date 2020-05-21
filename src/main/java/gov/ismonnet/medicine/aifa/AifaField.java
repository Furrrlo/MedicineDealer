package gov.ismonnet.medicine.aifa;

public enum AifaField {
    BUNDLE("bundle"),
    CODICE_FARMACO("sm_field_codice_farmaco"),
    DESCRIZIONE_FARMACO("sm_field_descrizione_farmaco"),
    CODICE_ATC("sm_field_codice_atc"),
    DESCRIZIONE_ATC("sm_field_descrizione_atc"),
    CODICE_DITTA("sm_field_codice_ditta"),
    DESCRIZIONE_DITTA("sm_field_descrizione_ditta"),
    TIPO_PROCEDURA("sm_field_tipo_procedura");

    private final String name;

    AifaField(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Fields{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
