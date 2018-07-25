package fkt.facets.util
/**
Specifies a unique identity.
 */
interface Identified {
  /**
  Return an object that is as far as possible unique for the implementation.

  This will usually be a class instance counter.
   */
  val identity:Any
}