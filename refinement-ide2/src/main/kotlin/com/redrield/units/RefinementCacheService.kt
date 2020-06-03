package com.redrield.units

import com.intellij.openapi.diagnostic.Logger
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtElement

class RefinementCacheService(private val srv: KotlinCacheService) : KotlinCacheService by srv {
    private val log = Logger.getInstance("#refinement.cacheService")
    override fun getResolutionFacade(elements: List<KtElement>): ResolutionFacade {
        val delegate = srv.getResolutionFacade(elements)

        return UnitsResolutionFacade(delegate)
    }
}