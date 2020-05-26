package gov.ismonnet.medicine.authentication;

import com.google.inject.BindingAnnotation;

import javax.inject.Qualifier;
import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@NameBinding
@BindingAnnotation
@Qualifier
public @interface AuthorizedDevice {
}
