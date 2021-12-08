package pl.databucket.api.java.examples;

import pl.databucket.api.java.client.*;
import pl.databucket.api.java.examples.constants.TagName;
import pl.databucket.api.java.examples.constants.UserProp;

public class RulesExamples extends DatabucketInitialization {

	public static void main(String[] args) {
		RulesExamples re = new RulesExamples();
//		re.example1();
//		re.example2();
//		re.example3();
		re.example4();
	}

	private void example1() {
		/* {
			"rules": [
				[ "tagId", "==", 1 ]
			]
		} */

		Data user = bucketDevUsers.getData(new Rules(Field.TAG_ID, Operator.equal, databucket.getTagId(TagName.GOOD)));
		System.out.println(user);
	}

	private void example2() {
		/*{
			"rules": [
				[ "tagId", "==", 1 ],
				[ "$.firstName", "like", "Jo%" ]
			]
		} */
		Rules rules = new Rules();
		rules.addRule(Field.TAG_ID, Operator.equal, databucket.getTagId(TagName.GOOD));
		rules.addRule(UserProp.FIRST_NAME, Operator.like, "Jo%");
		System.out.println(rules.toJsonString());

		Data user = bucketDevUsers.getData(rules);
		System.out.println(user);
	}

	private void example3() {
		/*{
			"rules": [
				{
					"or": [
						[ "tagId", "==", 1] ,
						[ "$.firstName", "like", "John%" ]
					]
				}
			]
		}*/
		Rules rules = new Rules(LogicalOperator.or);
		rules.addRule(Field.TAG_ID, Operator.equal, databucket.getTagId(TagName.GOOD));
		rules.addRule(UserProp.FIRST_NAME, Operator.like, "John%");
		System.out.println(rules.toJsonString());

		Data user = bucketDevUsers.getData(rules);
		System.out.println(user);
	}

	private void example4() {
		/*{
			"rules": [
				[ "tagId", "==", 1 ],
				[ "$.firstName", "like", "John%" ],
				{
					"or": [
						[ "$.lastName",	"~",	"B.*a" ],
						[ "$.lastName",	"similar",	"L(a|c).*" ]
					]
				}
			]
		} */
		Rules rules = new Rules();
		rules.addRule(Field.TAG_ID, Operator.equal, databucket.getTagId(TagName.GOOD));
		rules.addRule(UserProp.FIRST_NAME, Operator.like, "John%");

		Rules subRules = new Rules(LogicalOperator.or);
		subRules.addRule(UserProp.LAST_NAME, Operator.matchCaseSensitive, "B.*a");
		subRules.addRule(UserProp.LAST_NAME, Operator.similar, "L(a|c).*");

		rules.addSubRules(subRules);

		System.out.println(rules.toJsonString());

		Data user = bucketDevUsers.getData(rules);

	}
}