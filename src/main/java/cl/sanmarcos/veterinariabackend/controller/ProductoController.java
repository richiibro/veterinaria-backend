package cl.sanmarcos.veterinariabackend.controller;

import cl.sanmarcos.veterinariabackend.model.Producto;
import cl.sanmarcos.veterinariabackend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoRepository productoRepository;

    @GetMapping
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Producto producto) {
        String errorValidacion = validarProducto(producto);
        if (errorValidacion != null) {
            return ResponseEntity.badRequest().body(Map.of("error", errorValidacion));
        }

        producto.setNombre(producto.getNombre().trim());
        producto.setCategoria(producto.getCategoria().trim());
        Producto guardado = productoRepository.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Producto detalles) {
        String errorValidacion = validarProducto(detalles);
        if (errorValidacion != null) {
            return ResponseEntity.badRequest().body(Map.of("error", errorValidacion));
        }

        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(detalles.getNombre().trim());
            producto.setDescripcion(detalles.getDescripcion());
            producto.setPrecio(detalles.getPrecio());
            producto.setStock(detalles.getStock());
            producto.setCategoria(detalles.getCategoria().trim());
            producto.setImagenUrl(detalles.getImagenUrl());
            return ResponseEntity.ok(productoRepository.save(producto));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!productoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private String validarProducto(Producto producto) {
        if (producto == null || producto.getNombre() == null || producto.getNombre().isBlank()) {
            return "El nombre del producto es obligatorio.";
        }
        if (producto.getCategoria() == null || producto.getCategoria().isBlank()) {
            return "La categoría del producto es obligatoria.";
        }
        if (producto.getPrecio() == null || producto.getPrecio() <= 0) {
            return "El precio debe ser mayor a 0.";
        }
        if (producto.getStock() == null || producto.getStock() < 0) {
            return "El stock no puede ser negativo.";
        }
        return null;
    }
}