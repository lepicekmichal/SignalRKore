package eu.lepicekmichal.signalrkore

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskAction
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonElement

open class HubCommunicationTask : DefaultTask() {
    private val output: Provider<Directory> = project.layout.buildDirectory.dir("generated/kotlin")

    @TaskAction
    fun generate() {
        val genericTypeVariableT = 'T'.typeVariable(0, bounded = true)

        FileSpec.builder("eu.lepicekmichal.signalrkore", "HubCommunication")
            .indent(" ".repeat(4))
            .addImport("kotlinx.serialization", "serializer")
            .addType(
                TypeSpec.classBuilder("HubCommunication")
                    .addModifiers(KModifier.ABSTRACT)
                    .addFunction(
                        FunSpec.builder("toJson")
                            .addTypeVariable(genericTypeVariableT)
                            .receiver(genericTypeVariableT)
                            .addParameter("serializer", genericTypeVariableT.inKSerializer)
                            .addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
                            .returns(JsonElement::class)
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("fromJson")
                            .addTypeVariable(genericTypeVariableT.copy(bounds = emptyList()))
                            .receiver(JsonElement::class)
                            .addParameter("deserializer", genericTypeVariableT.inKSerializer)
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
                                    .defaultValue("%M()", MemberName("kotlin.collections", "emptyList"))
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
                                    .defaultValue("%M()", MemberName("kotlin.collections", "emptyList"))
                                    .build(),
                            )
                            .addModifiers(KModifier.ABSTRACT, KModifier.SUSPEND)
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("invoke")
                            .addTypeVariable(genericTypeVariableT)
                            .addParameter("method", String::class)
                            .addParameter("resultSerializer", genericTypeVariableT.inKSerializer)
                            .addParameter(name = "args", type = List::class.asTypeName().parameterizedBy(JsonElement::class.asTypeName()))
                            .addParameter(
                                parameterSpec = ParameterSpec.builder(
                                    name = "streams",
                                    type = List::class.asTypeName().parameterizedBy(
                                        Flow::class.asTypeName().parameterizedBy(JsonElement::class.asTypeName())
                                    ),
                                )
                                    .defaultValue("%M()", MemberName("kotlin.collections", "emptyList"))
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
                            .addParameter("itemSerializer", genericTypeVariableT.inKSerializer)
                            .addParameter(name = "args", type = List::class.asTypeName().parameterizedBy(JsonElement::class.asTypeName()))
                            .addParameter(
                                parameterSpec = ParameterSpec.builder(
                                    name = "streams",
                                    type = List::class.asTypeName().parameterizedBy(
                                        Flow::class.asTypeName().parameterizedBy(JsonElement::class.asTypeName())
                                    ),
                                )
                                    .defaultValue("%M()", MemberName("kotlin.collections", "emptyList"))
                                    .build(),
                            )
                            .addModifiers(KModifier.ABSTRACT)
                            .returns(Flow::class.asTypeName().parameterizedBy(genericTypeVariableT))
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("handleIncomingInvocation")
                            .addTypeVariable(genericTypeVariableT)
                            .receiver(Flow::class.asTypeName().parameterizedBy(ClassName("eu.lepicekmichal.signalrkore", "HubMessage.Invocation")))
                            .addParameter(name = "resultSerializer", type = genericTypeVariableT.inKSerializer)
                            .addParameter(
                                name = "callback",
                                type = LambdaTypeName
                                    .get(
                                        parameters = arrayOf(ClassName("eu.lepicekmichal.signalrkore", "HubMessage.Invocation")),
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
                            .returns(Flow::class.asTypeName().parameterizedBy(ClassName("eu.lepicekmichal.signalrkore", "HubMessage.Invocation")))
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("mapCatching")
                            .addTypeVariable(genericTypeVariableT)
                            .receiver(Flow::class.asTypeName().parameterizedBy(ClassName("eu.lepicekmichal.signalrkore", "HubMessage.Invocation")))
                            .addParameter(
                                name = "transform",
                                type = LambdaTypeName
                                    .get(
                                        parameters = arrayOf(ClassName("eu.lepicekmichal.signalrkore", "HubMessage.Invocation")),
                                        returnType = genericTypeVariableT,
                                    )
                                    .copy(suspending = true)
                            )
                            .addModifiers(KModifier.ABSTRACT)
                            .returns(Flow::class.asTypeName().parameterizedBy(genericTypeVariableT))
                            .build()
                    )
                    .addMapMethods()
                    .addOutMethods()
                    .addInMethods()
                    .build()
            )
            .build()
            .writeTo(output.get().asFile)
    }

    private fun TypeSpec.Builder.addMapMethods(): TypeSpec.Builder {
        val streamType = 'F'.typeVariable(0, bounded = true)
        return this.addFunction(
            FunSpec.builder("mapJsonStream")
                .addModifiers(listOf(KModifier.PRIVATE))
                .addTypeVariable(streamType)
                .receiver(Flow::class.asTypeName().parameterizedBy(streamType))
                .addParameter(
                    ParameterSpec.builder(
                        name = "streamSerializer",
                        type = streamType.inKSerializer,
                    ).build()
                )
                .returns(Flow::class.asTypeName().parameterizedBy(JsonElement::class.asTypeName()))
                .addCode("return %M { it.toJson(streamSerializer) }", MemberName("kotlinx.coroutines.flow", "map"))
                .build()
        )
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
            argsAndStreamsCombination(reifiedParameters = reified, bounded = true) { argumentTypes, streamTypes ->
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
            argsAndStreamsCombination(reifiedParameters = reified, bounded = true) { argumentTypes, streamTypes ->
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
            argsAndStreamsCombination(reifiedParameters = reified, bounded = true) { argumentTypes, streamTypes ->
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
            paramsCombination(reifiedParameters = false, bounded = false) { paramTypes ->
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
                            else -> ClassName(
                                "eu.lepicekmichal.signalrkore",
                                "OnValue${paramTypes.size}"
                            ).parameterizedBy(paramTypes)
                        }
                    ),
                    body = {
                        addCode(
                            buildString {
                                appendLine("return on(target = target, hasResult = false).mapCatching {")

                                if (paramTypes.isNotEmpty()) {
                                    appendLine("    OnValue${paramTypes.size}(")

                                    appendLine(paramTypes.passingInTypedParameters("deserializer", indent = " ".repeat(8)))
                                    appendLine("    )")
                                }

                                append("}")
                            }
                        )
                    }
                )
            }
        }
    }

    private fun TypeSpec.Builder.addOnWithResults(reified: Boolean): TypeSpec.Builder {
        return this.apply {
            val resultTypeVariable = 'R'.typeVariable(0, reified = reified, bounded = true)

            paramsCombination(reifiedParameters = reified, bounded = true) { paramTypes ->
                addInFunction2(
                    name = "on",
                    jvmName = "onWithResult",
                    modifiers = if (reified) listOf(KModifier.INLINE) else emptyList(),
                    hasReifiedTypes = reified,
                    paramTypes = paramTypes,
                    extraTypeVariables = listOf(resultTypeVariable),
                    extraParameters = listOfNotNull(
                        if (!reified) ParameterSpec("resultSerializer", resultTypeVariable.inKSerializer) else null,
                        ParameterSpec(
                            name = "callback",
                            type = LambdaTypeName.get(
                                parameters = paramTypes.toTypedArray(),
                                returnType = resultTypeVariable,
                            ).copy(suspending = true),
                            modifiers = if (reified) listOf(KModifier.NOINLINE) else emptyList(),
                        ),
                    ),
                    returns = Unit::class.asTypeName(),
                    body = {
                        if (!reified) {
                            addCode(
                                buildString {
                                    appendLine("on(target = target, hasResult = true)")
                                    appendLine("    .handleIncomingInvocation(")
                                    appendLine("        resultSerializer = resultSerializer,")
                                    appendLine("        callback = {")
                                    appendLine("            callback(")
                                    appendLine(paramTypes.passingInTypedParameters("deserializer", indent = " ".repeat(16)))
                                    appendLine("            )")
                                    appendLine("        },")
                                    append("    )")
                                }
                            )
                        } else {
                            addCode(
                                buildString {
                                    appendLine("on(")
                                    appendLine("    target = target,")
                                    appendLine("    resultSerializer = serializer<${resultTypeVariable.name}>(),")

                                    if (paramTypes.isNotEmpty())
                                        appendLine(paramTypes.passingInReifiedParameters("deserializer", indent = " ".repeat(4)))

                                    appendLine("    callback = callback,")
                                    append(")")
                                }
                            )
                        }
                    }
                )
            }
        }
    }

    private fun TypeSpec.Builder.addStreams(reified: Boolean): TypeSpec.Builder {
        return this.apply {
            val itemTypeVariable = 'R'.typeVariable(0, bounded = true)

            argsAndStreamsCombination(reifiedParameters = reified, bounded = true) { argumentTypes, streamTypes ->
                if (reified && argumentTypes.isEmpty() && streamTypes.isEmpty()) return@argsAndStreamsCombination

                addInFunction(
                    name = "stream",
                    modifiers = if (reified) listOf(KModifier.INLINE) else emptyList(),
                    hasReifiedTypes = reified,
                    argumentTypes = argumentTypes,
                    streamTypes = streamTypes,
                    extraTypeVariables = listOf(itemTypeVariable),
                    extraParameters = listOf(ParameterSpec("itemSerializer", itemTypeVariable.inKSerializer)),
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
        val resultTypeVariable = 'R'.typeVariable(0, bounded = true)

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
                                    name = "argSerializer${index + 1}",
                                    type = argumentType.inKSerializer,
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
                                    name = "streamSerializer${index + 1}",
                                    type = streamType.inKSerializer,
                                ).build()
                            } else null,
                        )
                    }
                )
                .addParameters(if (withResultType) listOf(ParameterSpec("resultSerializer", resultTypeVariable.inKSerializer)) else emptyList())
                .returns(if (withResultType) resultTypeVariable else Unit::class.asTypeName())
                .addStatement(
                    buildString {
                        appendLine("return $name(")
                        appendLine("    method = method,")

                        if (withResultType)
                            appendLine("    resultSerializer = resultSerializer,")

                        if (hasReifiedTypes)
                            appendLine(argumentTypes.passingInReifiedArguments("arg", indent = " ".repeat(4)))
                        else
                            appendLine(argumentTypes.passingInTypedArguments("arg", indent = " ".repeat(4)) { arg, type -> "$arg.toJson($type)" })

                        if (hasReifiedTypes) {
                            if (streamTypes.isNotEmpty())
                                appendLine(streamTypes.passingInReifiedArguments("stream", indent = " ".repeat(4)))
                        } else {
                            appendLine(streamTypes.passingInTypedArguments("stream", indent = " ".repeat(4)) { arg, type -> "$arg.mapJsonStream($type)" })
                        }

                        append(")")
                    },
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
                                    name = "argSerializer${index + 1}",
                                    type = argumentType.inKSerializer,
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
                                    name = "streamSerializer${index + 1}",
                                    type = streamType.inKSerializer,
                                ).build()
                            } else null,
                        )
                    }
                )
                .returns(returns)
                .addCode(
                    buildString {
                        appendLine("return %L(")
                        appendLine("    method = method,")
                        appendLine("    itemSerializer = itemSerializer,")

                        if (hasReifiedTypes)
                            appendLine(argumentTypes.passingInReifiedArguments("arg", indent = " ".repeat(4)))
                        else
                            appendLine(argumentTypes.passingInTypedArguments("arg", indent = " ".repeat(4)) { arg, type -> "$arg.toJson($type)" })

                        if (hasReifiedTypes) {
                            if (streamTypes.isNotEmpty())
                                appendLine(streamTypes.passingInReifiedArguments("stream", indent = " ".repeat(4)))
                        } else {
                            appendLine(streamTypes.passingInTypedArguments("stream", indent = " ".repeat(4)) { arg, type -> "$arg.mapJsonStream($type)" })
                        }

                        append(")")
                    },
                    name
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
                            name = "deserializer${index + 1}",
                            type = argumentType.inKSerializer,
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
        indent: String = "",
        passedValue: (arg: String, argType: String) -> String,
    ): String = buildString {
        if (this@passingInTypedArguments.isEmpty()) {
            appendLine("${name}s = emptyList(),")
        } else {
            appendLine("${name}s = listOf(")
            this@passingInTypedArguments.indices.forEach { index ->
                appendLine("    ${passedValue("$name${index + 1}", "${name}Serializer${index + 1}")},")
            }
            appendLine("),")
        }
    }.trim().prependIndent(indent)

    private fun List<TypeVariableName>.passingInTypedParameters(
        name: String,
        indent: String = "",
    ): String = buildString {
        this@passingInTypedParameters.indices.forEach { index ->
            appendLine("it.arguments[$index].fromJson(${name}${index + 1}),")
        }
    }.trim().prependIndent(indent)

    private fun List<TypeVariableName>.passingInReifiedArguments(
        name: String,
        indent: String = "",
    ): String = buildString {
        for ((index, parameter) in this@passingInReifiedArguments.withIndex()) {
            appendLine("$name${index + 1} = $name${index + 1},")
            appendLine("${name}Serializer${index + 1} = serializer<${parameter.name}>(),")
        }
    }.trim().prependIndent(indent)

    private fun List<TypeVariableName>.passingInReifiedParameters(
        name: String,
        indent: String = "",
    ): String = buildString {
        this@passingInReifiedParameters.forEachIndexed { index, parameter ->
            appendLine("${name}${index + 1} = serializer<${parameter.name}>(),")
        }
    }.trim().prependIndent(indent)

    private inline fun argsAndStreamsCombination(
        reifiedParameters: Boolean,
        bounded: Boolean,
        crossinline body: (argumentTypes: List<TypeVariableName>, streamTypes: List<TypeVariableName>) -> Unit,
    ) {
        val argsTypeVariables = 'T'.typeVariables(reified = reifiedParameters, bounded = bounded)
        val streamsTypeVariables = 'F'.typeVariables(reified = reifiedParameters, bounded = bounded)

        argsTypeVariables.combinatorics { tRes ->
            streamsTypeVariables.combinatorics { fRes ->
                body(tRes, fRes)
            }
        }
    }

    private inline fun paramsCombination(
        reifiedParameters: Boolean,
        bounded: Boolean,
        crossinline body: (paramTypes: List<TypeVariableName>) -> Unit,
    ) {
        val paramsTypeVariables = 'P'.typeVariables(reified = reifiedParameters, bounded = bounded)

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

    private fun Char.typeVariables(reified: Boolean, bounded: Boolean) = List(COMBINATION_LIMIT) { if (it == 0) null else typeVariable(it, reified, bounded) }

    private fun Char.typeVariable(index: Int = 0, reified: Boolean = false, bounded: Boolean = false): TypeVariableName {
        require(index >= 0)
        val typeVariable = TypeVariableName(name = "${this}${index}", bounds = if (bounded) listOf(Any::class) else emptyList())
        return if (reified) typeVariable.copy(reified = true) else typeVariable
    }

    private val TypeName.inKSerializer
        get() = KSerializer::class.asTypeName().parameterizedBy(this)

    companion object {
        private const val COMBINATION_LIMIT = 9
    }
}
