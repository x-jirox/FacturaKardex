package com.ista.FacturaKardex.controllers;


import com.ista.FacturaKardex.models.entity.Product;
import com.ista.FacturaKardex.models.service.IProductsService;
import com.ista.FacturaKardex.models.service.IUsuarioService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/product")
public class ProductsControllers {


    @Value("${spring.config.location}")
    private String rootPath;

    @Autowired
    private IProductsService productsService;

    @Autowired
    private IUsuarioService usuarioService;

    // Listar productos
    @RequestMapping(value = "/listarProducts", method = RequestMethod.GET)
    public String listarProduct(
            @RequestParam(name = "nombre", required = false, defaultValue = "") String nombre,
            Model model) {
        List<Product> productos;

        if (nombre.isEmpty()) {
            productos = productsService.findAll();
        } else {
            productos = productsService.findByNombreContainingIgnoreCase(nombre);
        }

        // Definir categorías predefinidas (o desde un servicio si es necesario)
        List<String> categorias = List.of("Botas", "Zandalias", "Casuales", "Deportivos", "Escolares");

        model.addAttribute("productos", productos);
        model.addAttribute("nombre", nombre);
        model.addAttribute("categorias", categorias); // Agregar las categorías al modelo

        return "product/listarProducts"; // Asegúrate de que esta ruta sea correcta
    }


    // Crear un producto
    @RequestMapping(value = "/formProduct")
    public String crearProducts(Map<String, Object> model) {
        Product producto = new Product();
        model.put("producto", producto);
        List<String> categorias = List.of("Botas", "Zandalias", "Casuales", "Deportivos", "Escolares");
        model.put("categorias", categorias);
        return "/product/formProduct";
    }

    // Guardar producto
    @RequestMapping(value = "/formProduct", method = RequestMethod.POST)
    public String guardarProducts(Product producto, @RequestParam("file") MultipartFile foto) {
        if (producto.getCantidad() < 1) {
            producto.setEstado(false); // Reposición
        } else {
            producto.setEstado(true); // Disponible
        }

        if (!foto.isEmpty()) {
            Path directFoto = Paths.get("C://workspaces//uploads");
            String rootPath = directFoto.toFile().getAbsolutePath();

            try {
                byte[] bytes = foto.getBytes();
                Path dirArchive = Paths.get(rootPath + "//" + foto.getOriginalFilename());
                Files.write(dirArchive, bytes);
                producto.setFoto(foto.getOriginalFilename());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            System.out.println("No hay imagen disponible");
        }

        productsService.save(producto);
        return "redirect:/product/listarProducts";
    }


    // Actualizar producto
    @RequestMapping(value = "/formProduct/{id}")
    public String actualizarProduct(@PathVariable(value = "id") Long id, Map<String, Object> model) {
        Product producto = productsService.findOne(id);
        // Definir categorías predefinidas
        List<String> categorias = List.of("Botas", "Sandalias", "Casuales", "Deportivos", "Escolares");
        model.put("categorias", categorias);
        if (producto == null) {
            return "redirect:/product/listarProducts";
        }
        model.put("producto", producto);
        return "/product/formProduct";
    }

    // Eliminar producto
    @RequestMapping(value = "/eliminarProducto/{id}")
    public String eliminarProduct(@PathVariable(value = "id") Long id) {
        if (id > 0) {
            productsService.delete(id);
        }
        return "redirect:/product/listarProducts";
    }

    // Ver producto
    @GetMapping(value = {"/verProduct/{id}"})
    public String verProduct(@PathVariable(value = "id") Long id, Map<String, Object> model) {
        Product producto = productsService.findOne(id);
        if (producto == null) {
            return "redirect:/product/listarProducts";
        }
        model.put("producto", producto);
        return "/product/verProduct";
    }

    // Buscar productos en el inventario
    @GetMapping("/buscarProducto")
    public String buscarProducto(@RequestParam("nombre") String nombre, Model model) {
        List<Product> productos = productsService.findByNombreContainingIgnoreCase(nombre);
        model.addAttribute("productos", productos);
        return "resultadoBusqueda";
    }

    // Buscar productos en shop
    @GetMapping("/buscar")
    public String buscarShop(@RequestParam("nombre") String nombre, Model model) {
        List<Product> productos = productsService.findByNombreContainingIgnoreCase(nombre);
        model.addAttribute("productos", productos);
        return "resultadoShop";
    }

    @GetMapping("/resultadoShop")
    public String mostrarResultados() {
        return "resultadoShop"; // Vista para mostrar los resultados
    }

    @RequestMapping("/reporteCliente")
    public String generarReporteClientes(@RequestParam(required = false) String categoria,
                                         @RequestParam(required = false) Boolean estado,
                                         @RequestParam(required = false) String nombre,
                                         @RequestParam(required = false) String talla,
                                         @RequestParam(required = false) Integer cantidad,
                                         Model model) {
        // Definir categorías predefinidas
        List<String> categorias = List.of("Botas", "Sandalias", "Casuales", "Deportivos", "Escolares");
        model.addAttribute("categorias", categorias);

        // Obtener productos filtrados
        List<Product> productos = productsService.findProductosByFiltros(categoria, estado, nombre, talla, cantidad);

        // Manejo del caso donde no hay productos encontrados
        if (productos.isEmpty()) {
            model.addAttribute("noProductos", "No se encontraron productos.");
        }

        model.addAttribute("productos", productos);
        model.addAttribute("selectedCategoria", categoria);
        model.addAttribute("selectedEstado", estado);
        model.addAttribute("selectedNombre", nombre);
        model.addAttribute("selectedTalla", talla);
        model.addAttribute("selectedCantidad", cantidad);

        return "index"; // Asegúrate de que "index" sea la vista correcta
    }

    @RequestMapping("/reporte")
    public String generarReporte(@RequestParam(required = false) String categoria,
                                 @RequestParam(required = false) Boolean estado,
                                 @RequestParam(required = false) String nombre,
                                 @RequestParam(required = false) String talla,
                                 @RequestParam(required = false) Integer cantidad,
                                 Model model) {

        // Definir categorías predefinidas
        List<String> categorias = List.of("Botas", "Zandalias", "Casuales", "Deportivos", "Escolares");
        model.addAttribute("categorias", categorias);

        // Filtra los productos con los parámetros proporcionados
        List<Product> productosFiltrados = productsService.findProductosByFiltros(categoria, estado, nombre, talla, cantidad);

        // Manejo del caso donde no hay productos encontrados
        if (productosFiltrados.isEmpty()) {
            model.addAttribute("noProductos", "No se encontraron productos con los filtros seleccionados.");
        }

        // Agregar atributos al modelo para la vista
        model.addAttribute("productos", productosFiltrados);
        model.addAttribute("selectedCategoria", categoria);
        model.addAttribute("selectedEstado", estado);
        model.addAttribute("selectedNombre", nombre);
        model.addAttribute("selectedTalla", talla);
        model.addAttribute("selectedCantidad", cantidad);

        return "/product/reporte";
    }




    // Generar reporte Excel
    @GetMapping("/reporte/excel")
    public void exportToExcel(@RequestParam(required = false) String categoria,
                              @RequestParam(required = false) Boolean estado,
                              @RequestParam(required = false) String nombre,
                              @RequestParam(required = false) String talla,
                              @RequestParam(required = false) Integer cantidad,
                              HttpServletResponse response, Model model) throws IOException {

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=productos.xlsx");

        // Definir categorías predefinidas
        List<String> categorias = List.of("Botas", "Zandalias", "Casuales", "Deportivos", "Escolares");
        model.addAttribute("categorias", categorias);


        List<Product> productosFiltrados = productsService.findProductosByFiltros(categoria, estado, nombre, talla, cantidad);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Productos");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Id");
        headerRow.createCell(1).setCellValue("Nombre");
        headerRow.createCell(2).setCellValue("Categoría");
        headerRow.createCell(3).setCellValue("Estado");
        headerRow.createCell(4).setCellValue("Talla");
        headerRow.createCell(5).setCellValue("Cantidad");

        int rowNum = 1;
        for (Product producto : productosFiltrados) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(producto.getId());
            row.createCell(1).setCellValue(producto.getNombre());
            row.createCell(2).setCellValue(producto.getCategoria());
            row.createCell(3).setCellValue(producto.getEstado() ? "Disponible" : "No disponible");
            row.createCell(4).setCellValue(producto.getTalla());
            row.createCell(5).setCellValue(producto.getCantidad());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
    //Reporte en pdf
    @GetMapping("/reporte/pdf")
        public void exportToPDF(@RequestParam(required = false) String categoria,
                            @RequestParam(required = false) Boolean estado,
                            @RequestParam(required = false) String nombre,
                            @RequestParam(required = false) String talla,
                            @RequestParam(required = false) Integer cantidad,
                            HttpServletResponse response, Model model) throws IOException {

        // Configuración de la respuesta HTTP
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=productos.pdf");
        // Definir categorías predefinidas
        List<String> categorias = List.of("Botas", "Zandalias", "Casuales", "Deportivos", "Escolares");
        model.addAttribute("categorias", categorias);
        // Obtener los productos filtrados
        List<Product> productosFiltrados = productsService.findProductosByFiltros(categoria, estado, nombre, talla, cantidad);

        // Crear el documento PDF
        PdfWriter pdfWriter = new PdfWriter(response.getOutputStream());
        PdfDocument pdfDoc = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDoc);

        // Añadir el título
        document.add(new Paragraph("Reporte de Productos"));

        // Crear la tabla
        Table table = new Table(8);
        table.addCell("Id");
        table.addCell("Nombre");
        table.addCell("Categoría");
        table.addCell("Descripción");
        table.addCell("Talla");
        table.addCell("Precio");
        table.addCell("Stock");
        table.addCell("Estado");

        // Rellenar la tabla con los datos
        for (Product producto : productosFiltrados) {
            table.addCell(String.valueOf(producto.getId()));
            table.addCell(producto.getNombre());
            table.addCell(producto.getCategoria());
            table.addCell(producto.getDescripcion());
            table.addCell(producto.getTalla());
            table.addCell(String.valueOf(producto.getPrecio()));
            table.addCell(String.valueOf(producto.getCantidad()));
            table.addCell(producto.getEstado() ? "Disponible" : "Reposición");
        }

        // Añadir la tabla al documento
        document.add(table);
        document.close();
    }

}
