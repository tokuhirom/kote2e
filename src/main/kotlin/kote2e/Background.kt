package kote2e

fun Background(cb: BackgroundContext.() -> Unit): BackgroundContext {
    val bg = BackgroundContext()
    bg.apply(cb)
    return bg
}