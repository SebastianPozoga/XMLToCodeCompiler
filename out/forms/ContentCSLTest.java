/* * To change this template, choose Tools | Templates * and open the template in the editor. */
package eu.pozoga.jspf.form;
import eu.pozoga.jspf.core.Form;
import eu.pozoga.jspf.core.Form.Massage;
import eu.pozoga.jspf.core.ServiceUnit;
import eu.pozoga.jspf.model.User;
import eu.pozoga.jspf.model.UsergroupManager;
import eu.pozoga.jspf.services.DBConnectorService;
import eu.pozoga.jspf.validators.EmailValidator;
import eu.pozoga.jspf.validators.LengthValidator;
import eu.pozoga.jspf.validators.RequiredValidator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.Session;
/** * * @author sebastian */
public class ContentForm extends Form  {
    public ContentForm(Content entity) {
        super(entity);
    }
    @Override
    public boolean validate() {
        boolean isValid = true;
        Content entity = getEntity();
        List <Massage> massage;
        //Properties
        //title
        massage = getMassages("Title");
        Title title = entity.getTitle();
        isValid = isValid & LengthValidator.validate(massage, 5, 500, title());
        isValid = isValid & RequiredValidator.validate(massage, entity.getTitle());
        //description
        massage = getMassages("Description");
        Description description = entity.getDescription();
        isValid = isValid & LengthValidator.validate(massage, 5, 250, description());
        //keywords
        massage = getMassages("Keywords");
        Keywords keywords = entity.getKeywords();
        isValid = isValid & LengthValidator.validate(massage, 5, 250, keywords());
        //body
        massage = getMassages("Body");
        Body body = entity.getBody();
        isValid = isValid & LengthValidator.validate(massage, 1, 50000, body());
        isValid = isValid & RequiredValidator.validate(massage, entity.getBody());
        setValid(isValid);
        return isValid;
    }
}
