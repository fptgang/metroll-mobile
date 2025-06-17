package com.vidz.data.mapper

/**
 * A base mapper interface that provides mapping from an external model to a domain model.
 *
 * [D] represents the domain model type.
 * [E] represents the external model type (e.g., remote, local).
 */
interface BaseMapper<D, E> {
    /**
     * Maps an external model to a domain model.
     * @param external The external model instance.
     * @return The corresponding domain model instance.
     */
    fun toDomain(external: E): D

    /**
     * Maps a list of external models to a list of domain models.
     * @param externalList The list of external model instances.
     * @return The list of corresponding domain model instances.
     */
    fun toDomainList(externalList: List<E>): List<D> {
        return externalList.map { toDomain(it) }
    }
}

/**
 * A base mapper interface that provides bidirectional mapping between
 * domain models and remote data layer models.
 *
 * [D] represents the domain model type.
 * [R] represents the remote model type.
 */
interface BaseRemoteMapper<D, R> : BaseMapper<D, R> {
    /**
     * Maps a domain model to a remote model.
     * @param domain The domain model instance.
     * @return The corresponding remote model instance.
     */
    fun toRemote(domain: D): R

    /**
     * Maps a list of domain models to a list of remote models.
     * @param domainList The list of domain model instances.
     * @return The list of corresponding remote model instances.
     */
    fun toRemoteList(domainList: List<D>): List<R> {
        return domainList.map { toRemote(it) }
    }
}

/**
 * A base mapper interface that provides bidirectional mapping between
 * domain models and local data layer models.
 *
 * [D] represents the domain model type.
 * [L] represents the local model type.
 */
interface BaseLocalMapper<D, L> : BaseMapper<D, L> {
    /**
     * Maps a domain model to a local model.
     * @param domain The domain model instance.
     * @return The corresponding local model instance.
     */
    fun toLocal(domain: D): L

    /**
     * Maps a list of domain models to a list of local models.
     * @param domainList The list of domain model instances.
     * @return The list of corresponding local model instances.
     */
    fun toLocalList(domainList: List<D>): List<L> {
        return domainList.map { toLocal(it) }
    }
}