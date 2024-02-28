package ua.torchers.roadmapai.roadmap

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.torchers.roadmapai.roadmap.scaffold.model.Roadmap
import ua.torchers.roadmapai.shared.NotFoundException

class RoadmapContentNotGenerated(roadmap: Roadmap, node: Roadmap.RmNode) :
    Exception("Roadmap`s ${roadmap.name} $node don`t have content yet")

val roadmapNotFoundException = NotFoundException("Roadmap")

fun <T : Roadmap> Mono<T>.orRoadmapNotFound() = this.switchIfEmpty(Mono.error(roadmapNotFoundException))
fun <T : Roadmap> Flux<T>.orRoadmapNotFound() = this.switchIfEmpty(Flux.error(roadmapNotFoundException))