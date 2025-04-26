package com.example.GuitarApp.util.validators;

import com.example.GuitarApp.entity.AbstractEntity;
import com.example.GuitarApp.util.exceptions.UniqueFieldValidatorConfigurationException;
import com.example.GuitarApp.util.validators.annotation.UniqueField;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.example.GuitarApp.util.validators.ValidationUtils.addFieldViolation;


@Component
public class UniqueFieldValidator implements ConstraintValidator<UniqueField, Object> {

    private final ApplicationContext applicationContext;

    private Class<? extends JpaRepository<? extends AbstractEntity, Integer>> repoClass;
    private String fieldName;
    private String idField;
    private boolean canUpdate;

    @Value("${strings.exists-method.name.no-id}")
    private String EXISTS_METHOD_NO_ID_STRING;
    @Value("${strings.exists-method.name.with-id}")
    private String EXISTS_METHOD_WITH_ID_STRING;

    @Autowired
    public UniqueFieldValidator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize(UniqueField annotation) {
        this.repoClass = annotation.repository();
        this.fieldName = annotation.fieldName();
        this.idField = annotation.idField();
        this.canUpdate = annotation.canUpdate();
    }

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        try {
            Object fieldValue = getFieldValue(dto, fieldName);
            if (fieldValue == null) return true;

            JpaRepository<? extends AbstractEntity, Integer> repository = applicationContext.getBean(repoClass);

            boolean exists = canUpdate ?
                    checkExistsWithId(repository, dto, fieldValue)
                    : checkExistsWithoutId(repository, fieldValue);

            if (!exists) return true;

            addFieldViolation(context, fieldName);
            return false;

        } catch (ReflectiveOperationException e) {
            throw new UniqueFieldValidatorConfigurationException(
                    "Failed during unique field validation. Field: " + fieldName + ". " + e.getMessage(), e
            );
        }
    }

    private boolean checkExistsWithId(JpaRepository<? extends AbstractEntity, Integer> repository, Object dto, Object fieldValue)
            throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        int idValue = (int) getFieldValue(dto, idField);
        String methodName = String.format(EXISTS_METHOD_WITH_ID_STRING, capitalized(fieldName), capitalized(idField));
        Method method = repoClass.getMethod(methodName, fieldValue.getClass(), int.class);

        return (boolean) method.invoke(repository, fieldValue, idValue);
    }

    private boolean checkExistsWithoutId(JpaRepository<? extends AbstractEntity, Integer> repository, Object fieldValue)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        String methodName = String.format(EXISTS_METHOD_NO_ID_STRING, capitalized(fieldName));
        Method method = repoClass.getMethod(methodName, fieldValue.getClass());

        return (boolean) method.invoke(repository, fieldValue);
    }

    private Object getFieldValue(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    private String capitalized(String fieldName) {
        return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

    //TODO: тут варто додати перевірку наявності репо і методів через @PostConstruct це складно.
}
