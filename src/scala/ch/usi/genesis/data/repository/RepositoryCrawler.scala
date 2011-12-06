package ch.usi.genesis.data.repository


trait RepositoryCrawler {
  def crawl(firstRev: Int, lastRev: Int, step: Int)
  def crawl(firstRev : Int)
}