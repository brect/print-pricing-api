package br.com.printpricing.materials.services

import br.com.printpricing.exceptions.ResourceNotFoundException
import br.com.printpricing.materials.entities.FilamentMaterial
import br.com.printpricing.materials.repositories.FilamentMaterialRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FilamentMaterialService(private val repository: FilamentMaterialRepository) {
    fun create(material: FilamentMaterial): FilamentMaterial = repository.save(material)
    fun findAll(): List<FilamentMaterial> = repository.findAll()
    fun findById(id: Long): FilamentMaterial =
        repository.findById(id).orElseThrow { ResourceNotFoundException("Material", id) }

    @Transactional
    fun update(id: Long, payload: FilamentMaterial): FilamentMaterial {
        val current = findById(id)
        current.brand = payload.brand
        current.type = payload.type
        current.spoolCost = payload.spoolCost
        current.spoolWeightKg = payload.spoolWeightKg
        current.color = payload.color
        return repository.save(current)
    }

    @Transactional
    fun delete(id: Long) {
        repository.delete(findById(id))
    }
}
