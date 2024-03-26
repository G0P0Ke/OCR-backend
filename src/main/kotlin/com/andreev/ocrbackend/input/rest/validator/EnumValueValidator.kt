package com.andreev.ocrbackend.input.rest.validator

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.properties.Delegates
import kotlin.reflect.KClass

class EnumValueValidator : ConstraintValidator<EnumValue, CharSequence> {

    private lateinit var acceptedValues: Set<String>
    private var ignoreCase by Delegates.notNull<Boolean>()

    override fun initialize(constraintAnnotation: EnumValue) {
        val enumConstants = constraintAnnotation.enumClass
            .java.enumConstants

        acceptedValues = if (constraintAnnotation.field == "") {
            enumConstants.map { it.name }
        } else {
            val field = constraintAnnotation.enumClass.java.getDeclaredField(constraintAnnotation.field)
            field.isAccessible = true
            enumConstants.map { field.get(it).toString() }
        }.toSet()

        ignoreCase = constraintAnnotation.ignoreCase
    }

    override fun isValid(value: CharSequence?, context: ConstraintValidatorContext): Boolean {
        if (value == null) {
            return true
        }

        val isValid = acceptedValues.any { it.equals(value.toString(), ignoreCase = ignoreCase) }
        if (!isValid) {
            context.disableDefaultConstraintViolation()
            val errorMessage = "must be any of $acceptedValues"
            context.buildConstraintViolationWithTemplate(errorMessage)
                .addConstraintViolation()
        }

        return isValid
    }
}

class IterableEnumValueValidator : ConstraintValidator<EnumValue, Iterable<CharSequence>> {

    private val enumValueValidator = EnumValueValidator()

    override fun initialize(constraintAnnotation: EnumValue) {
        enumValueValidator.initialize(constraintAnnotation)
    }

    override fun isValid(value: Iterable<CharSequence>?, context: ConstraintValidatorContext): Boolean {
        return value?.map { enumValueValidator.isValid(it, context) }?.all { it } ?: true
    }
}

@Suppress("unused")
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
@Constraint(validatedBy = [EnumValueValidator::class, IterableEnumValueValidator::class])
annotation class EnumValue(
    val enumClass: KClass<out Enum<*>>,
    val field: String = "",
    val ignoreCase: Boolean = false,
    val message: String = "",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
