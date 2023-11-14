package com.github.yandoroshenko.cqrs.dto

import com.github.yandoroshenko.cqrs.model.Order

case class OrderDTO(orderId: String, total: Double, totalString: String)

object OrderDTO {
  def apply(order: Order): OrderDTO =
    new OrderDTO(order.orderId.id, order.total, s"$$${order.total}")
}
