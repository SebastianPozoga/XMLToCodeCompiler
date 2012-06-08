/* To change this template, choose Tools | Templates * and open the template in the editor. */
package eu.pozoga.jspf.form;
import eu.pozoga.jspf.core.Form;
import eu.pozoga.jspf.core.Form.Massage;
import eu.pozoga.jspf.core.ServiceUnit;
import eu.pozoga.jspf.model.Content;
import java.util.List;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
/** * * @author sebastian */
public class ContentForm extends Form< Content> {
    public ContentForm(Content entity, String namespace) {
        super(entity, namespace);
    }
    @Override
    public boolean validate() {        /*         * Init variables         */
        boolean isValid = true;
        List<Massage> massage;
        Content
        entity = getEntity();
        //title
        String title = entity.getTitle();
        //description
        String description = entity.getDescription();
        //keywords
        String keywords = entity.getKeywords();
        //body
        text body = entity.getBody();
        setValid(isValid);
        return isValid;
    }
    @Override
    public void decode(ServletRequest req, String baseName) throws Throwable {
        DBConnectorService cs = (DBConnectorService) ServiceUnit.getService((HttpServletRequest) req, null, DBConnectorService.class);
        Session session = cs.getSession();
        //Decode
        loadString("setName", req.getParameter(baseName +"Name"));
        loadString("setPassword", req.getParameter(baseName +"Password"));
        loadString("setViewname", req.getParameter(baseName +"Viewname"));
        loadString("setFirstname", req.getParameter(baseName +"Firstname"));
        loadString("setLastname", req.getParameter(baseName +"Lastname"));
        loadString("setEmail", req.getParameter(baseName +"Email"));
        loadEntity("setUsergroup", req.getParameter(baseName +"Usergroup"), session, UsergroupManager.getInstance());
    }
    public void decode(HttpServletRequest req) throws Throwable {
        decode(req, getNamespace());
    }
}
