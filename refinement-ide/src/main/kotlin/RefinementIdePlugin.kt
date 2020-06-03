import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.IdePlugin
import arrow.meta.ide.invoke
import com.intellij.openapi.diagnostic.Logger
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtElement

private val LOG = Logger.getInstance("#arrow.ideMain")

val IdeMetaPlugin.refinementIde: IdePlugin
    get() = "Refinement Units IDE Companion" {
        listOf(
            addProjectService(KotlinCacheService::class.java) { _, srv ->
                srv?.let(::unitsTypeckCacheService)
            },
            addDiagnosticSuppressor {  }
        )
    }

private fun unitsTypeckCacheService(srv: KotlinCacheService): KotlinCacheService {
    return object : KotlinCacheService by srv {
        override fun getResolutionFacade(elements: List<KtElement>): ResolutionFacade {
            val delegateFacade = srv.getResolutionFacade(elements)

            return UnitsResolutionFacade(delegateFacade)
        }
    }
}
