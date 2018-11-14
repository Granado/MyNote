package com.granado.groovy.learning

class MyDelegate {
  def func = {
    println('hello')
  }
}
def c = {
  func()
}
c.delegate = new MyDelegate()
c.call()