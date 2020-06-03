import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.KotlinType

class UnitsResolutionFacade(private val delegate: ResolutionFacade) : ResolutionFacade by delegate {
    override fun analyze(element: KtElement, bodyResolveMode: BodyResolveMode): BindingContext {
        val default = delegate.analyze(element, bodyResolveMode)
        return object : BindingContext by default {
            override fun getType(expr: KtExpression): KotlinType? {
                val ty = default.getType(expr) ?: return null
                val name = ty.getJetTypeFqName(false)
                if (name.contains("Add") || name.contains("Sub") || name.contains("Mul")
                    || name.contains("Div")
                ) {
                    return refineNumericType(ty, moduleDescriptor)
                } else {
                    return ty
                }
            }
        }
//        if(element is KtDeclaration) {
//            val ty = element.type() ?: return default
//            val name = ty.getJetTypeFqName(true)
//
//e       } else {
//            return default
//        }
    }
}