package gov.ismonnet.medicine.jaxb;

import org.jvnet.jaxb2_commons.lang.DefaultToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

public class ToStringStrategy extends DefaultToStringStrategy {

    @Override
    protected void appendClassName(StringBuilder buffer, Object object) {
        if(object != null)
            buffer.append(getShortClassName(object.getClass()));
    }

    @Override
    public boolean isUseIdentityHashCode() {
        return false;
    }

    @Override
    protected void appendContentStart(StringBuilder buffer) {
        buffer.append('{');
    }

    @Override
    protected void appendContentEnd(StringBuilder buffer) {
        buffer.append('}');
    }

    @Override
    protected void appendArrayStart(StringBuilder buffer) {
        buffer.append('[');
    }

    @Override
    protected void appendArrayEnd(StringBuilder buffer) {
        buffer.append(']');
    }

    @Override
    protected void appendArraySeparator(StringBuilder buffer) {
        super.appendArraySeparator(buffer);
        buffer.append(' ');
    }

    @Override
    protected void appendNullText(StringBuilder buffer) {
        buffer.append("null");
    }

    @Override
    protected void appendFieldStart(ObjectLocator parentLocator, Object parent, String fieldName, StringBuilder buffer) {
        if (fieldName != null) {
            buffer.append(fieldName);
            buffer.append("=");
            buffer.append("'");
        }
    }

    @Override
    protected void appendFieldStart(ObjectLocator parentLocator, Object parent, String fieldName, StringBuilder buffer, boolean valueSet) {
        if (fieldName != null) {
            buffer.append(fieldName);
            buffer.append("=");
            buffer.append("'");
        }
    }

    @Override
    protected void appendFieldEnd(ObjectLocator parentLocator, Object parent, String fieldName, StringBuilder buffer) {
        if (fieldName != null)
            buffer.append("'");
        super.appendFieldEnd(parentLocator, parent, fieldName, buffer);
    }

    @Override
    protected void appendFieldEnd(ObjectLocator parentLocator, Object parent, String fieldName, StringBuilder buffer, boolean valueSet) {
        if (fieldName != null)
            buffer.append("'");
        super.appendFieldEnd(parentLocator, parent, fieldName, buffer, valueSet);
    }
}

