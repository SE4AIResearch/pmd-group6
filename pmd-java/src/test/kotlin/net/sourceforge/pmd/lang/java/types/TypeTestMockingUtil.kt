/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import net.sourceforge.pmd.lang.ast.NodeStream
import net.sourceforge.pmd.lang.ast.NodeStream.*
import net.sourceforge.pmd.lang.java.JavaParsingHelper
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger
import org.mockito.Mockito.*
import org.mockito.Mockito


// kept out of TypeTestUtil so as not to import Mockito there

fun JavaParsingHelper.parseWithTypeInferenceSpy(code: String): Pair<ASTCompilationUnit, TypeInferenceSpy> {
    val spy = spy(typeInfLogger)
    val acu = this.logTypeInference(spy).parse(code)
    return Pair(acu, TypeInferenceSpy(spy, acu.typeSystem))
}

/**
 * Spies on the logger.
 *
 * @property spy A [Mockito.spy], you can call methods of mockito there
 */
data class TypeInferenceSpy(private val spy: TypeInferenceLogger, val ts: TypeSystem) {
    private fun shouldHaveNoErrors() {
        // note that inexact method ref selection may call noApplicableCandidates
        // or noCompileTimeDeclaration sometimes I think.
        verify(spy, never()).ambiguityError(any(), any(), any())
        verify(spy, never()).noCompileTimeDeclaration(any())
        verify(spy, never()).fallbackInvocation(any(), any())
    }

    fun shouldBeOk(block: TypeDslMixin.() -> Unit) {
        this.shouldHaveNoErrors()
        TypeDslOf(ts).block()
        this.shouldHaveNoErrors()
    }

    fun shouldTriggerMissingCtDecl(block: TypeDslMixin.() -> Unit) {
        this.shouldHaveNoErrors()
        TypeDslOf(ts).block()
        verify(spy, times(1)).noCompileTimeDeclaration(any())
    }

    fun shouldBeAmbiguous(block: TypeDslMixin.() -> Unit) {
        this.shouldHaveNoErrors()
        TypeDslOf(ts).block()
        verify(spy, times(1)).ambiguityError(any(), any(), any())
    }
}


fun JavaNode.methodDeclarations(): DescendantNodeStream<ASTMethodDeclaration> = descendants(ASTMethodDeclaration::class.java)
fun JavaNode.typeDeclarations(): DescendantNodeStream<ASTAnyTypeDeclaration> = descendants(ASTAnyTypeDeclaration::class.java)
fun JavaNode.firstEnclosingType() = descendants(ASTAnyTypeDeclaration::class.java).firstOrThrow().typeMirror
fun JavaNode.ctorDeclarations(): DescendantNodeStream<ASTConstructorDeclaration> = descendants(ASTConstructorDeclaration::class.java)

fun JavaNode.declaredTypeSignatures() = descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }
fun JavaNode.declaredMethodSignatures() = methodDeclarations().toList { it.sig }

fun JavaNode.methodCalls(): DescendantNodeStream<ASTMethodCall> = descendants(ASTMethodCall::class.java)
fun JavaNode.firstMethodCall() = descendants(ASTMethodCall::class.java).firstOrThrow()

fun JavaNode.ctorCalls(): DescendantNodeStream<ASTConstructorCall> = descendants(ASTConstructorCall::class.java)
fun JavaNode.firstCtorCall() = descendants(ASTConstructorCall::class.java).firstOrThrow()

fun JavaNode.typeVariables(): MutableList<JTypeVar> = descendants(ASTTypeParameter::class.java).toList { it.typeMirror }
fun JavaNode.varAccesses(name: String): NodeStream<ASTVariableAccess> = descendants(ASTVariableAccess::class.java).filter { it.name == name }
fun JavaNode.varId(name: String) = descendants(ASTVariableDeclaratorId::class.java).filter { it.name == name }.firstOrThrow()
fun JavaNode.typeVar(name: String) = descendants(ASTTypeParameter::class.java).filter { it.parameterName == name }.firstOrThrow().typeMirror
