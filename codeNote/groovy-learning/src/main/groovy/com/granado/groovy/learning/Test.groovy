package com.granado.groovy.learning

class test {

  public test() {
    println 'i was initialized'
  }

  def echo = { it ->
    println "i tell you: ${it}"
  }

}

println 'test'

def t = new test()
t.echo '哈哈哈哈'
t.echo('哈哈哈哈')
t.echo.call("Are you kidding me?")