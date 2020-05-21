package gov.ismonnet.medicine.aifa;

public class AifaQuery {

    private final String query;

    public AifaQuery(String query) {
        this.query = query;
    }

    public String getQueryString() {
        return query;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final StringBuilder sb = new StringBuilder();

        private Builder() {}

        public Condition param(AifaField field) {
            return new Condition(field);
        }

        public AifaQuery build() {
            return new AifaQuery(sb.toString());
        }

        public class Condition {

            private final AifaField field;

            public Condition(AifaField field) {
                this.field = field;
            }

            private void appendQueryStart() {
                if(sb.length() > 0)
                    sb.append("+");
                sb.append(field.getName());
                sb.append(':');
            }

            public Builder eq(Object o) {
                appendQueryStart();
                sb.append(o.toString());
                return Builder.this;
            }

            public Builder startsWith(Object o) {
                appendQueryStart();
                sb.append(o.toString());
                sb.append('*');
                return Builder.this;
            }

            public Builder endsWith(Object o) {
                appendQueryStart();
                sb.append('*');
                sb.append(o.toString());
                return Builder.this;
            }

            public Builder contains(Object o) {
                appendQueryStart();
                sb.append('*');
                sb.append(o.toString());
                sb.append('*');
                return Builder.this;
            }
        }
    }
}
