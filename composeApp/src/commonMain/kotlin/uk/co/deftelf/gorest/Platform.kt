package uk.co.deftelf.gorest

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform