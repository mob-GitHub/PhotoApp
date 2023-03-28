package si.f5.mob.photoapp.main

import si.f5.mob.mediastore.entity.Image

data class GridItemData(
    val image: Image,
    var isSelected: Boolean = false,
)