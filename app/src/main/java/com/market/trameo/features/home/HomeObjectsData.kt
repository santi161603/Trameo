package com.market.trameo.features.home

data class HomeObjectItem(
    val id: Int,
    val title: String,
    val category: String,
    val location: String,
    val points: Int,
    val description: String,
    val imageUrl: String
)

object HomeObjectsData {
    val objects: List<HomeObjectItem> = listOf(
        HomeObjectItem(
            id = 1,
            title = "Bicicleta urbana",
            category = "Movilidad",
            location = "Medellin",
            points = 120,
            description = "Bicicleta en buen estado, ideal para recorridos cortos.",
            imageUrl = "https://picsum.photos/seed/trameo-bike/800/500"
        ),
        HomeObjectItem(
            id = 2,
            title = "Licuadora 2L",
            category = "Hogar",
            location = "Bogota",
            points = 80,
            description = "Licuadora funcional con vaso de vidrio.",
            imageUrl = "https://picsum.photos/seed/trameo-blender/800/500"
        ),
        HomeObjectItem(
            id = 3,
            title = "Guitarra acustica",
            category = "Musica",
            location = "Cali",
            points = 150,
            description = "Incluye funda, cuerdas nuevas y afinador basico.",
            imageUrl = "https://picsum.photos/seed/trameo-guitar/800/500"
        ),
        HomeObjectItem(
            id = 4,
            title = "Set de libros",
            category = "Educacion",
            location = "Barranquilla",
            points = 60,
            description = "Coleccion variada de literatura y desarrollo personal.",
            imageUrl = "https://picsum.photos/seed/trameo-books/800/500"
        )
    )
}
