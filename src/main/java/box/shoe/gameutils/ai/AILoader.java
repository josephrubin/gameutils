package box.shoe.gameutils.ai;

import android.content.res.AssetManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class AILoader
{
    public static AI fromXml(AssetManager assetManager, String xmlFilePath) throws IOException
    {
        // We will use a DOM parser. Create the factory with settings as unobtrusive as possible.
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        documentBuilderFactory.setNamespaceAware(false);
        documentBuilderFactory.setIgnoringComments(true);
        documentBuilderFactory.setValidating(false);

        // Use the factory to create the builder to parse our file, then parse into a Document.
        DocumentBuilder xmlDocumentBuilder;
        try
        {
            xmlDocumentBuilder = documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            throw new IllegalArgumentException("ParserConfigurationException occurred: " + e.getMessage());
        }
        Document xmlDocument;
        try
        {
            xmlDocument = xmlDocumentBuilder.parse(assetManager.open(xmlFilePath));
        }
        catch (SAXException e)
        {
            throw new IllegalArgumentException("SAXException occurred while parsing XML: " + e.getMessage());
        }

        // Get the root 'AI' element.
        Element root = xmlDocument.getDocumentElement();

        // Load up the Behavior definitions.
        // Force one copy of each Behavior. Map name to Behavior.
        Map<String, Behavior> behaviors = new HashMap<>();
        // Remember the starting Behavior (definition with starting="true")
        Behavior startingBehavior = null;
        // Immediately populate the map from the Behavior tags.
        NodeList behaviorNodeList = root.getElementsByTagName("behavior");
        int behaviorNodeListLength = behaviorNodeList.getLength();
        for (int i = 0; i < behaviorNodeListLength; i++)
        {
            Element behaviorElement = (Element) behaviorNodeList.item(i);
            String behaviorName = behaviorElement.getAttribute("name");
            Behavior behavior = (Behavior) getInstanceFromClassName(stripWhiteSpace(behaviorElement.getAttribute("class")));
            if (behaviors.containsKey(behaviorName))
            {//todo: should be some kind of xml exception
                throw new IllegalArgumentException("Redundant <behavior> definition: " + behaviorName);
            }
            else
            {
                behaviors.put(behaviorName, behavior);
                if (behaviorElement.getAttribute("starting").equals("true"))
                {
                    startingBehavior = behavior;
                }
            }
            //todo: remember the starting behavior
        }
        if (startingBehavior == null)
        {
            throw new IllegalArgumentException("No starting=\"true\" <behavior> was defined!");
        }

        // Load up the Condition definitions.
        // Force one copy of each Condition. Map name to Condition.
        Map<String, Condition> conditions = new HashMap<>();
        // Immediately populate the map from the Behavior tags.
        NodeList conditionNodeList = root.getElementsByTagName("condition");
        int conditionNodeListLength = conditionNodeList.getLength();
        for (int i = 0; i < conditionNodeListLength; i++)
        {
            Element conditionElement = (Element) conditionNodeList.item(i);
            String conditionName = conditionElement.getAttribute("name");
            Condition condition = (Condition) getInstanceFromClassName(stripWhiteSpace(conditionElement.getAttribute("class")));
            if (conditions.containsKey(conditionName))
            {//todo: should be some kind of xml exception
                throw new IllegalArgumentException("Redundant <condition> definition: " + conditionName);
            }
            else
            {
                conditions.put(conditionName, condition);
            }
        }

        NodeList clauseNodeList = root.getElementsByTagName("clause");
        int clauseNodeListLength = clauseNodeList.getLength();
        Clause[] clauses = new Clause[clauseNodeListLength];
        for (int i = 0; i < clauseNodeListLength; i++)
        {
            // Each clause will have at least one Premise and at least one Predicate.
            Element clauseElement = (Element) clauseNodeList.item(i);
            NodeList premiseNodeList = clauseElement.getElementsByTagName("premise");
            int premiseNodeListLength = premiseNodeList.getLength();
            NodeList predicateNodeList = clauseElement.getElementsByTagName("predicate");
            int predicateNodeListLength = predicateNodeList.getLength();
            if (premiseNodeListLength <= 0)
            {
                throw new IllegalArgumentException("Each clause must have at least one Premise: " + clauseElement.getAttribute("name"));
            }
            if (predicateNodeListLength <= 0)
            {
                throw new IllegalArgumentException("Each clause must have at least one Predicate: " + clauseElement.getAttribute("name"));
            }

            Premise[] premises = new Premise[premiseNodeListLength];
            for (int j = 0; j < premiseNodeListLength; j++)
            {
                Element premiseElement = (Element) premiseNodeList.item(j);
                String premiseBehaviorName = premiseElement.getAttribute("behavior");
                Premise premise = new Premise(behaviors.get(premiseBehaviorName));
                premises[j] = premise;
            }

            Predicate[] predicates = new Predicate[predicateNodeListLength];
            for (int j = 0; j < predicateNodeListLength; j++)
            {
                Element predicateElement = (Element) predicateNodeList.item(j);
                String predicateConditionName = predicateElement.getAttribute("condition");
                String predicateResultType = predicateElement.getAttribute("result");
                Result predicateResult = null;
                String resultOutcomeBehaviorName;
                switch (predicateResultType.toLowerCase())
                {
                    case "pop":
                        predicateResult = new Result.PopResult();
                        break;
                    case "push":
                        resultOutcomeBehaviorName = ((Element) predicateElement.getElementsByTagName("outcome")
                                .item(0))
                                .getAttribute("behavior");
                        predicateResult = new Result.PushResult(new Outcome(behaviors.get(resultOutcomeBehaviorName)));
                        break;
                    case "swap":
                        resultOutcomeBehaviorName = ((Element) predicateElement.getElementsByTagName("outcome")
                                .item(0))
                                .getAttribute("behavior");
                        predicateResult = new Result.SwapResult(new Outcome(behaviors.get(resultOutcomeBehaviorName)));
                        break;
                }
                Predicate predicate = new Predicate(conditions.get(predicateConditionName), predicateResult);
                predicates[j] = predicate;
            }

            Clause clause = new Clause(premises, predicates);
            clauses[i] = clause;
        }

        return new AI(startingBehavior, clauses);
    }

    private static Object getInstanceFromClassName(String text)
    { //TODO: do something useful with each exception
        Object object = null;
        try
        {
            object = Class.forName(stripWhiteSpace(text)).getConstructor().newInstance();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException("Class (" + stripWhiteSpace(text) + ") has no default constructor!");
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalArgumentException("Class (" + stripWhiteSpace(text) + ") not found!");
        }
        return object;
    }

    private static String stripWhiteSpace(String string)
    {
        return string.replaceAll("\\s","");
    }
}
