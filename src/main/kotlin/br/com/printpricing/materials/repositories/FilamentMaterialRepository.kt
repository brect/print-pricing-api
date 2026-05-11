package br.com.printpricing.materials.repositories

import br.com.printpricing.materials.entities.FilamentMaterial
import org.springframework.data.jpa.repository.JpaRepository

interface FilamentMaterialRepository : JpaRepository<FilamentMaterial, Long>
