val emptySeq = Seq.empty[String]

emptySeq match {
  case Nil => println("match Nil")
}

emptySeq match {
  case Seq() => println("match Seq()")
}