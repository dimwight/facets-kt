package fkt.facets.util
/**
Specifies a unique identity.
 */
interface Identified_ {
  /**
  Return an object that is as far as possible unique for the implementation.
  <p>This will usually be a class instance counter.
   */
  fun identity():Any
}