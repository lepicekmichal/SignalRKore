package eu.lepicekmichal.signalrkore

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonElement
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskAction
import kotlin.reflect.KClass

open class HubCommunicationTask : DefaultTask() {

    private val output: Provider<Directory> = project.layout.buildDirectory.dir("generated/kotlin")

    @TaskAction
    fun generate() {
        val genericTypeVariableT = TypeVariableName(name = "T", bounds = listOf(Any::class))

        FileSpec.builder("eu.lepicekmichal.signalrkore", "HubCommunication")
            .indent("    ")
            .addImport(
                packageName = "kotlinx.coroutines.flow",
                "map",
            )
            .addType(
                TypeSpec.classBuilder("HubCommunication")
                    .addModifiers(KModifier.ABSTRACT)
                    .addFunction(
                        FunSpec.builder("toJson")
                            .addTypeVariable(genericTypeVariableT)
                            .receiver(genericTypeVariableT)
                            .addParameter("kClass", genericTypeVariableT.inKClass)
                            .addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
                            .returns(JsonElement::class)
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("fromJson")
                            .addTypeVariable(genericTypeVariableT)
                            .receiver(JsonElement::class)
                            .addParameter("kClass", genericTypeVariableT.inKClass)
                            .addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
                            .returns(genericTypeVariableT)
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("send")
                            .addParameter("method", String::class)
                            .addParameter(name = "args", type = List::class.asTypeName().parameterizedBy(JsonElement::class.asTypeName()))
                            .addParameter(
                                parameterSpec = ParameterSpec.builder(
                                    name = "streams",
                                    type = List::class.asTypeName().parameterizedBy(
                                        Flow::class.asTypeName().parameterizedBy(JsonElement::class.asTypeName())
                                    ),
                                )
                                    .defaultValue("%L", "emptyList()")
                                    .build(),
                            )
                            .addModifiers(KModifier.ABSTRACT)
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("invoke")
                            .addParameter("method", String::class)
                            .addParameter(name = "args", type = List::class.asTypeName().parameterizedBy(JsonElement::class.asTypeName()))
                            .addParameter(
                                parameterSpec = ParameterSpec.builder(
                                    name = "streams",
                                    type = List::class.asTypeName().parameterizedBy(
                                        Flow::class.asTypeName().parameterizedBy(JsonElement::class.asTypeName())
                                    ),
                                )
                                    .defaultValue("%L", "emptyList()")
                                    .build(),
                            )
                            .addModifiers(KModifier.ABSTRACT, KModifier.SUSPEND)
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("invoke")
                            .addTypeVariable(genericTypeVariableT)
                            .addParameter("method", String::class)
                            .addParameter("resultType", genericTypeVariableT.inKClass)
                            .addParameter(name = "args", type = List::class.asTypeName().parameterizedBy(JsonElement::class.asTypeName()))
                            .addParameter(
                                parameterSpec = ParameterSpec.builder(
                                    name = "streams",
                                    type = List::class.asTypeName().parameterizedBy(
                                        Flow::class.asTypeName().parameterizedBy(JsonElement::class.asTypeName())
                                    ),
                                )
                                    .defaultValue("%L", "emptyList()")
                                    .build(),
                            )
                            .addModifiers(KModifier.ABSTRACT, KModifier.SUSPEND)
                            .returns(genericTypeVariableT)
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("stream")
                            .addTypeVariable(genericTypeVariableT)
                            .addParameter("method", String::class)
                            .addParameter("itemType", genericTypeVariableT.inKClass)
                            .addParameter(name = "args", type = List::class.asTypeName().parameterizedBy(JsonElement::class.asTypeName()))
                            .addParameter(
                                parameterSpec = ParameterSpec.builder(
                                    name = "streams",
                                    type = List::class.asTypeName().parameterizedBy(
                                        Flow::class.asTypeName().parameterizedBy(JsonElement::class.asTypeName())
                                    ),
                                )
                                    .defaultValue("%L", "emptyList()")
                                    .build(),
                            )
                            .addModifiers(KModifier.ABSTRACT)
                            .returns(Flow::class.asTypeName().parameterizedBy(genericTypeVariableT))
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("handleIncomingInvocation")
                            .addTypeVariable(genericTypeVariableT)
                            .receiver(Flow::class.asTypeName().parameterizedBy(TypeVariableName(name = "HubMessage.Invocation")))
                            .addParameter(name = "resultType", type = genericTypeVariableT.inKClass)
                            .addParameter(
                                name = "callback", type = LambdaTypeName
                                    .get(
                                        parameters = listOf(ParameterSpec.unnamed(TypeVariableName(name = "HubMessage.Invocation"))),
                                        returnType = genericTypeVariableT,
                                    )
                                    .copy(suspending = true)
                            )
                            .addModifiers(KModifier.ABSTRACT)
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("on")
                            .addParameter(name = "target", type = String::class.asTypeName())
                            .addParameter(name = "hasResult", type = Boolean::class.asTypeName())
                            .addModifiers(KModifier.ABSTRACT)
                            .returns(Flow::class.asTypeName().parameterizedBy(TypeVariableName(name = "HubMessage.Invocation")))
                            .build()
                    )
                    .addOutMethods()
                    .addInMethods()
                    .build()
            )
            .build()
            .writeTo(output.get().asFile)
    }

    private fun TypeSpec.Builder.addOutMethods(): TypeSpec.Builder {
        return this
            .addSends(reified = false)
            .addSends(reified = true)
            .addInvokes(reified = false)
            .addInvokes(reified = true)
            .addResultedInvokes(reified = false)
            .addResultedInvokes(reified = true)
    }

    private fun TypeSpec.Builder.addInMethods(): TypeSpec.Builder {
        return this
            .addOns()
            .addOnWithResults(reified = false)
            .addOnWithResults(reified = true)
            .addStreams(reified = false)
            .addStreams(reified = true)
    }

    private fun TypeSpec.Builder.addSends(reified: Boolean): TypeSpec.Builder {
        return this.apply {
            argsAndStreamsCombination(reifiedParameters = reified) { argumentTypes, streamTypes ->
                if (reified && argumentTypes.isEmpty() && streamTypes.isEmpty()) return@argsAndStreamsCombination

                addOutFunction(
                    name = "send",
                    modifiers = if (reified) listOf(KModifier.INLINE) else emptyList(),
                    hasReifiedTypes = reified,
                    argumentTypes = argumentTypes,
                    streamTypes = streamTypes,
                    withResultType = false,
                )
            }
        }
    }

    private fun TypeSpec.Builder.addInvokes(reified: Boolean): TypeSpec.Builder {
        return this.apply {
            argsAndStreamsCombination(reifiedParameters = reified) { argumentTypes, streamTypes ->
                if (reified && argumentTypes.isEmpty() && streamTypes.isEmpty()) return@argsAndStreamsCombination

                addOutFunction(
                    name = "invoke",
                    modifiers = listOfNotNull(KModifier.SUSPEND, if (reified) KModifier.INLINE else null),
                    hasReifiedTypes = reified,
                    argumentTypes = argumentTypes,
                    streamTypes = streamTypes,
                    withResultType = false,
                )
            }
        }
    }

    private fun TypeSpec.Builder.addResultedInvokes(reified: Boolean): TypeSpec.Builder {
        return this.apply {
            argsAndStreamsCombination(reifiedParameters = reified) { argumentTypes, streamTypes ->
                if (reified && argumentTypes.isEmpty() && streamTypes.isEmpty()) return@argsAndStreamsCombination

                addOutFunction(
                    name = "invoke",
                    jvmName = "invokeWithResult",
                    modifiers = listOfNotNull(KModifier.SUSPEND, if (reified) KModifier.INLINE else null),
                    hasReifiedTypes = reified,
                    argumentTypes = argumentTypes,
                    streamTypes = streamTypes,
                    withResultType = true,
                )
            }
        }
    }

    private fun TypeSpec.Builder.addOns(): TypeSpec.Builder {
        return this.apply {
            paramsCombination(reifiedParameters = false) { paramTypes ->
                addInFunction2(
                    name = "on",
                    modifiers = emptyList(),
                    hasReifiedTypes = false,
                    paramTypes = paramTypes,
                    extraTypeVariables = emptyList(),
                    extraParameters = emptyList(),
                    returns = Flow::class.asTypeName().parameterizedBy(
                        when (paramTypes.size) {
                            0 -> Unit::class.asTypeName()
                            else -> TypeVariableName(
                                name = "OnValue${paramTypes.size}${
                                    paramTypes.joinToString(prefix = "<", postfix = ">") {
                                        it.name
                                    }
                                }"
                            )
                        }
                    ),
                    body = {
                        addStatement(
                            """
                            |return 
                            |   on(target = target, hasResult = false).map {
                            ${
                                if (paramTypes.isNotEmpty()) "|      OnValue${paramTypes.size}(" else ""
                            }
                            ${
                                paramTypes.passingInTypedParameters("param")
                            }
                            ${
                                if (paramTypes.isNotEmpty()) "|      )" else ""
                            }
                            |   }
                            |""".trimMargin()
                        )
                    }
                )
            }
        }
    }

    private fun TypeSpec.Builder.addOnWithResults(reified: Boolean): TypeSpec.Builder {
        return this.apply {
            val resultTypeVariable = TypeVariableName(name = "RESULT", bounds = listOf(Any::class)).copy(reified = reified)

            paramsCombination(reifiedParameters = reified) { paramTypes ->
                addInFunction2(
                    name = "on",
                    jvmName = "onWithResult",
                    modifiers = if (reified) listOf(KModifier.INLINE) else emptyList(),
                    hasReifiedTypes = reified,
                    paramTypes = paramTypes,
                    extraTypeVariables = listOf(resultTypeVariable),
                    extraParameters = listOfNotNull(
                        if (!reified) ParameterSpec("resultType", resultTypeVariable.inKClass) else null,
                        ParameterSpec(
                            name = "callback",
                            type = LambdaTypeName.get(
                                parameters = paramTypes.mapIndexed { index, paramType ->
                                    ParameterSpec.builder(
                                        name = "param${index + 1}",
                                        type = paramType,
                                    ).build()
                                },
                                returnType = resultTypeVariable,
                            ).copy(suspending = true),
                            modifiers = if (reified) listOf(KModifier.NOINLINE) else emptyList(),
                        ),
                    ),
                    returns = Unit::class.asTypeName(),
                    body = {
                        if (!reified) {
                            addStatement("%L", "on(target = target, hasResult = true)")
                                .addCode(
                                    format = "%L",
                                    """
                                    |   .handleIncomingInvocation(
                                    |       resultType = resultType,
                                    |       callback = {
                                    |           callback(
                                    |              ${paramTypes.passingInTypedParameters("param")}
                                    |           )
                                    |       },
                                    |   )
                                    |""".trimMargin()
                                )
                        } else {
                            addStatement(
                                format = "%L",
                                """
                                |on(
                                |    target = target,
                                |    resultType = ${resultTypeVariable.name}::class,
                                |    ${paramTypes.passingInReifiedParameters("param")}
                                |    callback = callback,
                                |)                                        
                                |""".trimMargin(),
                            )
                        }
                    }
                )
            }
        }
    }

    private fun TypeSpec.Builder.addStreams(reified: Boolean): TypeSpec.Builder {
        return this.apply {
            val itemTypeVariable = TypeVariableName(name = "ITEM", bounds = listOf(Any::class))

            argsAndStreamsCombination(reifiedParameters = reified) { argumentTypes, streamTypes ->
                if (reified && argumentTypes.isEmpty() && streamTypes.isEmpty()) return@argsAndStreamsCombination

                addInFunction(
                    name = "stream",
                    modifiers = if (reified) listOf(KModifier.INLINE) else emptyList(),
                    hasReifiedTypes = reified,
                    argumentTypes = argumentTypes,
                    streamTypes = streamTypes,
                    extraTypeVariables = listOf(itemTypeVariable),
                    extraParameters = listOf(ParameterSpec("itemType", itemTypeVariable.inKClass)),
                    returns = Flow::class.asTypeName().parameterizedBy(itemTypeVariable),
                )
            }
        }
    }

    private fun TypeSpec.Builder.addOutFunction(
        name: String,
        jvmName: String = name,
        modifiers: List<KModifier>,
        hasReifiedTypes: Boolean,
        withResultType: Boolean,
        argumentTypes: List<TypeVariableName>,
        streamTypes: List<TypeVariableName>,
    ): TypeSpec.Builder {
        val resultTypeVariable = TypeVariableName(name = "RESULT", bounds = listOf(Any::class))

        return addFunction(
            FunSpec.builder(name = name)
                .apply {
                    if (name != jvmName) {
                        addAnnotation(AnnotationSpec.builder(JvmName::class).addMember("\"$jvmName\"").build())
                    }
                }
                .addModifiers(modifiers)
                .addTypeVariables(argumentTypes)
                .addTypeVariables(streamTypes)
                .addTypeVariables(if (withResultType) listOf(resultTypeVariable) else emptyList())
                .addParameter("method", String::class)
                .addParameters(
                    argumentTypes.flatMapIndexed { index, argumentType ->
                        listOfNotNull(
                            ParameterSpec.builder(
                                name = "arg${index + 1}",
                                type = argumentType,
                            ).build(),
                            if (!hasReifiedTypes) {
                                ParameterSpec.builder(
                                    name = "argType${index + 1}",
                                    type = argumentType.inKClass,
                                ).build()
                            } else null,
                        )
                    }
                )
                .addParameters(
                    streamTypes.flatMapIndexed { index, streamType ->
                        listOfNotNull(
                            ParameterSpec.builder(
                                name = "stream${index + 1}",
                                type = Flow::class.asTypeName().parameterizedBy(streamType),
                            ).build(),
                            if (!hasReifiedTypes) {
                                ParameterSpec.builder(
                                    name = "streamType${index + 1}",
                                    type = streamType.inKClass,
                                ).build()
                            } else null,
                        )
                    }
                )
                .addParameters(if (withResultType) listOf(ParameterSpec("resultType", resultTypeVariable.inKClass)) else emptyList())
                .returns(if (withResultType) resultTypeVariable else Unit::class.asTypeName())
                .addStatement(
                    """
                    |return $name(
                    |    method = method,
                    ${
                        if (withResultType) "|    resultType = resultType," else ""
                    }
                    ${
                        if (hasReifiedTypes) argumentTypes.passingInReifiedArguments("arg")
                        else argumentTypes.passingInTypedArguments("arg") { arg, type -> "$arg.toJson($type)" }
                    }
                    ${
                        if (hasReifiedTypes) streamTypes.passingInReifiedArguments("stream")
                        else streamTypes.passingInTypedArguments("stream") { arg, type -> "$arg.map { it.toJson($type) }" }
                    }
                    |)
                    |""".trimMargin(),
                )
                .build()
        )
    }

    private fun TypeSpec.Builder.addInFunction(
        name: String,
        jvmName: String = name,
        modifiers: List<KModifier>,
        hasReifiedTypes: Boolean,
        argumentTypes: List<TypeVariableName>,
        streamTypes: List<TypeVariableName>,
        extraTypeVariables: List<TypeVariableName> = emptyList(),
        extraParameters: List<ParameterSpec> = emptyList(),
        returns: TypeName,
    ): TypeSpec.Builder {
        return addFunction(
            FunSpec.builder(name = name)
                .apply {
                    if (name != jvmName) {
                        addAnnotation(AnnotationSpec.builder(JvmName::class).addMember("\"$jvmName\"").build())
                    }
                }
                .addModifiers(modifiers)
                .addTypeVariables(extraTypeVariables)
                .addTypeVariables(argumentTypes)
                .addTypeVariables(streamTypes)
                .addParameter("method", String::class)
                .addParameters(extraParameters)
                .addParameters(
                    argumentTypes.flatMapIndexed { index, argumentType ->
                        listOfNotNull(
                            ParameterSpec.builder(
                                name = "arg${index + 1}",
                                type = argumentType,
                            ).build(),
                            if (!hasReifiedTypes) {
                                ParameterSpec.builder(
                                    name = "argType${index + 1}",
                                    type = argumentType.inKClass,
                                ).build()
                            } else null,
                        )
                    }
                )
                .addParameters(
                    streamTypes.flatMapIndexed { index, streamType ->
                        listOfNotNull(
                            ParameterSpec.builder(
                                name = "stream${index + 1}",
                                type = Flow::class.asTypeName().parameterizedBy(streamType),
                            ).build(),
                            if (!hasReifiedTypes) {
                                ParameterSpec.builder(
                                    name = "streamType${index + 1}",
                                    type = streamType.inKClass,
                                ).build()
                            } else null,
                        )
                    }
                )
                .returns(returns)
                .addStatement(
                    """
                    |return $name(
                    |    method = method,
                    |    itemType = itemType,
                    ${
                        if (hasReifiedTypes) argumentTypes.passingInReifiedArguments("arg")
                        else argumentTypes.passingInTypedArguments("arg") { arg, type -> "$arg.toJson($type)" }
                    }
                    ${
                        if (hasReifiedTypes) streamTypes.passingInReifiedArguments("stream")
                        else streamTypes.passingInTypedArguments("stream") { arg, type -> "$arg.map { it.toJson($type) }" }
                    }
                    |)
                    |""".trimMargin(),
                )
                .build()
        )
    }

    private fun TypeSpec.Builder.addInFunction2(
        name: String,
        jvmName: String = name,
        modifiers: List<KModifier>,
        hasReifiedTypes: Boolean,
        paramTypes: List<TypeVariableName>,
        extraTypeVariables: List<TypeVariableName> = emptyList(),
        extraParameters: List<ParameterSpec> = emptyList(),
        returns: TypeName,
        body: FunSpec.Builder.() -> FunSpec.Builder,
    ): TypeSpec.Builder {
        return addFunction(
            FunSpec.builder(name = name)
                .apply {
                    if (name != jvmName) {
                        addAnnotation(AnnotationSpec.builder(JvmName::class).addMember("\"$jvmName\"").build())
                    }
                }
                .addModifiers(modifiers)
                .addTypeVariables(paramTypes)
                .addTypeVariables(extraTypeVariables)
                .addParameter("target", String::class)
                .addParameters(
                    if (!hasReifiedTypes) paramTypes.mapIndexed { index, argumentType ->
                        ParameterSpec.builder(
                            name = "paramType${index + 1}",
                            type = argumentType.inKClass,
                        ).build()
                    } else emptyList(),
                )
                .addParameters(extraParameters)
                .returns(returns)
                .body()
                .build()
        )
    }

    private fun List<TypeVariableName>.passingInTypedArguments(
        name: String,
        passedValue: (arg: String, argType: String) -> String,
    ): String =
        buildString {
            if (this@passingInTypedArguments.isEmpty()) {
                appendLine("|    ${name}s = emptyList(),")
            } else {
                appendLine("|    ${name}s = listOf(")
                this@passingInTypedArguments.indices.forEach { index ->
                    appendLine("|        ${passedValue("$name${index + 1}", "${name}Type${index + 1}")},")
                }
                appendLine("|    ),")
            }
        }

    private fun List<TypeVariableName>.passingInTypedParameters(
        name: String,
    ): String = buildString {
        this@passingInTypedParameters.indices.forEach { index ->
            appendLine("it.arguments[$index].fromJson(${name}Type${index + 1}),")
        }
    }

    private fun List<TypeVariableName>.passingInReifiedArguments(
        name: String,
    ): String = buildString {
        this@passingInReifiedArguments.forEachIndexed { index, parameter ->
            appendLine("$name${index + 1} = $name${index + 1},")
            appendLine("${name}Type${index + 1} = ${parameter.name}::class,")
        }
    }

    private fun List<TypeVariableName>.passingInReifiedParameters(
        name: String,
    ): String = buildString {
        this@passingInReifiedParameters.forEachIndexed { index, parameter ->
            appendLine("${name}Type${index + 1} = ${parameter.name}::class,")
        }
    }

    private inline fun argsAndStreamsCombination(
        reifiedParameters: Boolean,
        crossinline body: (argumentTypes: List<TypeVariableName>, streamTypes: List<TypeVariableName>) -> Unit,
    ) {
        val argsTypeVariables = 'T'.typeVariables(reified = reifiedParameters)
        val streamsTypeVariables = 'F'.typeVariables(reified = reifiedParameters)

        argsTypeVariables.combinatorics { tRes ->
            streamsTypeVariables.combinatorics { fRes ->
                body(tRes, fRes)
            }
        }
    }

    private inline fun paramsCombination(
        reifiedParameters: Boolean,
        crossinline body: (paramTypes: List<TypeVariableName>) -> Unit,
    ) {
        val paramsTypeVariables = 'P'.typeVariables(reified = reifiedParameters)

        paramsTypeVariables.combinatorics { body(it) }
    }

    private inline fun List<TypeVariableName?>.combinatorics(
        body: (variables: List<TypeVariableName>) -> Unit,
    ) {
        runningFold(emptyList<TypeVariableName>()) runningFoldF@{ acc, variable ->
            val resAcc = acc.plus(variable).filterNotNull()
            body(resAcc)
            resAcc
        }
    }

    private fun Char.typeVariables(reified: Boolean) = List(COMBINATION_LIMIT) {
        if (it == 0) null
        else TypeVariableName(name = "${this}$it", bounds = listOf(Any::class)).copy(reified = reified)
    }

    private val TypeName.inKClass
        get() = KClass::class.asTypeName().parameterizedBy(this)

    companion object {
        private const val DOLLAR_SIGN = "\$"
        private const val COMBINATION_LIMIT = 9
    }
}
