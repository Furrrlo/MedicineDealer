package gov.ismonnet.medicine.jaxb;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

public class InJarSchemaResolver extends BaseSchemaResolver {

    private final String basePath;

    public InJarSchemaResolver(Charset charset, String basePath) {
        super(charset);

        this.basePath = basePath;
    }

    @Override
    protected InputStream getSchemaInputStream(String name) {
        return getClass().getClassLoader().getResourceAsStream(basePath + File.separator + name);
    }
}
