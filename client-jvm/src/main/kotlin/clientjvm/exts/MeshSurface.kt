package clientjvm.exts

import godot.core.PoolColorArray
import godot.core.VariantArray
import godot.core.Vector3
import godot.core.variantArrayOf

fun surfaceArray(
    ARRAY_VERTEX: VariantArray<Vector3>? = null,
    ARRAY_NORMAL: VariantArray<Nothing>? = null,
    ARRAY_TANGENT: VariantArray<Nothing>? = null,
    ARRAY_COLOR: PoolColorArray? = null,
    ARRAY_TEX_UV: VariantArray<Nothing>? = null,
    ARRAY_TEX_UV2: VariantArray<Nothing>? = null,
    ARRAY_BONES: VariantArray<Nothing>? = null,
    ARRAY_WEIGHTS: VariantArray<Nothing>? = null,
    ARRAY_INDEX: VariantArray<Nothing>? = null,
): VariantArray<Any?> {
    return variantArrayOf(
        ARRAY_VERTEX, // 0 - ARRAY_VERTEX
        ARRAY_NORMAL, // 1 - ARRAY_NORMAL
        ARRAY_TANGENT, // 2 - ARRAY_TANGENT
        ARRAY_COLOR, // 3 - ARRAY_COLOR
        ARRAY_TEX_UV, // 4 - ARRAY_TEX_UV
        ARRAY_TEX_UV2, // 5 - ARRAY_TEX_UV2
        ARRAY_BONES, // 6 - ARRAY_BONES
        ARRAY_WEIGHTS, // 7 - ARRAY_WEIGHTS
        ARRAY_INDEX, // 8 - ARRAY_INDEX
    )
}
