/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package CSLCompiler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sebastian
 */
public abstract class CSLCompilerSchema {

   /*
    * Const
    */
   public static final String STANDARD_TAGS = "standards";

   /*
    * Variables
    */
   private List<Tag> allTags = new ArrayList<Tag>();
   private Map<String, Tag> groupTag = new HashMap<String, Tag>();
   private List<Replace> replaces = new ArrayList<Replace>();
   private List<ErrorSequence> errorSequences = new ArrayList<ErrorSequence>();

   /*
    * Classes
    */
    static public class Tag {
        String openTag;
        String openTagEnd=">";
        String endTag;
        String endTagEnd=null;

        String openCode;
        String endCode;

        //String myparser(StringBody)
        Method parseBody=null;

        String specialInsideOneExpresion=null;
        String specialInsideOneExpresionCode=null;

        String insideTags=STANDARD_TAGS;

        public Tag(String openTag, String endTag, String openCode, String endCode) {
            this.openTag = openTag;
            this.endTag = endTag;
            this.openCode = openCode;
            this.endCode = endCode;
        }
    }

   static public class Replace {
        String from;
        String to;

        public Replace(String from, String to) {
            this.from = from;
            this.to = to;
        }
        
    }

   static public class ErrorSequence {
        String sequence;
        String error;

        public ErrorSequence(String sequence, String error) {
            this.sequence = sequence;
            this.error = error;
        }
        
    }



   public void add(Tag tag, String group){
       allTags.add(tag);
       groupTag.put(group, tag);
   }

   public void add(Replace replace){
       replaces.add(replace);
   }

   public void add(ErrorSequence errorSequence){
       errorSequences.add(errorSequence);
   }



   int maxSequenceSize=0;
   public int getMaxSequenceSize(){
       if(maxSequenceSize==0){
           //get max
           int max = 0;
           for(Tag tag : allTags){
               if(tag.openTag.length()>max){
                   max=tag.openTag.length();
               }
               if(tag.specialInsideOneExpresion!=null && tag.specialInsideOneExpresion.length()>max){
                   max=tag.specialInsideOneExpresion.length();
               }
           }
           for(Replace r : replaces){
               if(r.from.length()>max){
                   max=r.from.length();
               }
           }
           for(ErrorSequence e : errorSequences){
               if(e.sequence.length()>max){
                   max=e.sequence.length();
               }
           }
           maxSequenceSize=max;
       }
       return maxSequenceSize;
   }

}
