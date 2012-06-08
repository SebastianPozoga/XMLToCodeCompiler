package jspfmetadatacompiler;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * SoftHashMap
 */

public final class SoftHashMap extends AbstractMap {
   private Map hash = new HashMap();
   private final ReferenceQueue queue = new ReferenceQueue();

   public SoftHashMap() {
   }

    @Override
   public Object get(Object key) {
      Object res = null;
      SoftReference sr = (SoftReference)hash.get(key);
      if ( sr != null ) {
         res = sr.get();
         if ( res == null )
            hash.remove(key);
      }
      return res;
   }

   private void processQueue() {
      for ( ;; ) {
         SoftValue sv = (SoftValue)queue.poll();
         if ( sv != null )
            hash.remove(sv.key);
         else
            return;
      }
   }

    @Override
   public Object put(Object key, Object value) {
      processQueue();
      return hash.put(key, new SoftValue(value, key, queue));
   }

    @Override
   public Object remove(Object key) {
      processQueue();
      return hash.remove(key);
   }

    @Override
   public void clear() {
      processQueue();
      hash.clear();
   }

    @Override
   public int size() {
      processQueue();
      return hash.size();
   }

   public Set entrySet() {
      /** @todo Figure this out */
      throw new UnsupportedOperationException();
   }


   /**
    * SoftValue
    */

   private static class SoftValue extends SoftReference {
      private final Object key;

      private SoftValue(Object k, Object key, ReferenceQueue q) {
         super(k, q);
         this.key = key;
      }
   }
}
