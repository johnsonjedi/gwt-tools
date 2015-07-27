/*
 * Copyright 2015 Johnson Hsu 
 * 
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *  http://www.gnu.org/licenses/lgpl-3.0.txt 
 *  
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *  See the License for the specific language governing permissions and 
 *  limitations under the License. 
 */
package idv.john.gwt.tools;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import idv.john.gwt.tools.annotation.EventAction;
import idv.john.gwt.tools.annotation.GwtEventRegistration;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 *
 * @author johnson
 */
@SupportedAnnotationTypes({"idv.john.gwt.tools.annotation.GwtEventRegistration"})
public class EventAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element e : roundEnv.getElementsAnnotatedWith(GwtEventRegistration.class)) {
            System.out.println("Processing " + e.toString());
            String packageName = getPackage(e);
            File eventDir = getEventDir(packageName);
            
            GwtEventRegistration reg = e.getAnnotation(GwtEventRegistration.class);
            
            File eventFile = new File(eventDir.getAbsolutePath() + File.separator + 
                        reg.name() + ".java");
            try {
                if (!eventFile.exists()) {
                    eventFile.createNewFile();
                }
                
                Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
                cfg.setDefaultEncoding("UTF-8");
                ClassTemplateLoader ctl = new ClassTemplateLoader(EventAnnotationProcessor.class, "template");
                cfg.setTemplateLoader(ctl);
                Template temp = cfg.getTemplate("Event.java.template");
                Writer writer = new OutputStreamWriter(new FileOutputStream(eventFile));
                        
                Map root = new HashMap();
                Map project = new HashMap();
                root.put("project", project);
                project.put("licensePath", "");
                root.put("package", packageName);
                root.put("user", "johnson");
                root.put("name", reg.name());
                
                List actions = new ArrayList();
                for (EventAction a : reg.actions()) {
                    Map action = new HashMap();
                    action.put("name", a.name());
                    action.put("comment", a.comment());
                    actions.add(action);
                }
                root.put("actions", actions);
                
                temp.process(root, writer);
            } catch (IOException | TemplateException ex) {
                Logger.getLogger(EventAnnotationProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }

    private String getPackage(Element e) {
        String path = e.toString();
        path = path.substring(0, path.lastIndexOf("."));
        path = path.substring(0, path.lastIndexOf("."));
        String packageName = path + ".event";
        return packageName;
    }

    private File getEventDir(String packageName) {
        File f = new File("target" + File.separator + "generated-sources" + 
                File.separator + "gwt" + File.separator + 
                packageName.replace(".", File.separator));
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }
    
}
