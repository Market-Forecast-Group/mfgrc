package com.mfg.chart.model.mdb;

import java.io.File;
import java.io.IOException;
import org.mfg.mdb.runtime.*;
import java.util.*;

/* BEGIN USER IMPORTS */
/* User can insert his code here */
/* END USER IMPORTS */

public class BaseChartMDBSession
/* BEGIN USER SESSION HINERITANCE */
extends MDBSession
/* END USER SESSION HINERITANCE */ 		{
 	
 	private static final Map<String,String> SIGNATURES;
 	private static final String SCHEMA_JSON = "{\"name\":\"BaseChart\",\"source\":\"\",\"packageName\":\"mdb\",\"bufferSize\":100,\"tables\":[{\"uuid\":\"1b11d903-f4b0-4aa5-b5d0-3665d45163e3\",\"name\":\"Bands\",\"columns\":[{\"name\":\"time\",\"uuid\":\"4488c076-8b2e-492b-99ba-8a9ffa73eed3\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"topPrice\",\"uuid\":\"19fb83f6-3ed5-4ff2-b281-4f7aab13937d\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"centerPrice\",\"uuid\":\"123e327d-14ca-4a02-a14e-f51b752d87d3\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"bottomPrice\",\"uuid\":\"284c7bba-8bc7-490b-9fca-ca30cff93b4d\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"physicalTime\",\"uuid\":\"0745ee9c-7d4c-4e62-9102-1c498bedbe9c\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"topRaw\",\"uuid\":\"7fbbbc88-8201-4583-a6ec-439bbc80be70\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"centerRaw\",\"uuid\":\"b4233eec-23c6-4880-8747-14fb2d3efd59\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"bottomRaw\",\"uuid\":\"ffec2584-3796-43c5-8ac8-e05c091fa567\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"3999c52c-e8c2-450b-8ac7-fd297d84e419\",\"name\":\"Pivot\",\"columns\":[{\"name\":\"pivotPrice\",\"uuid\":\"c850cfc3-e382-4309-92c7-daee7917d67a\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"pivotTime\",\"uuid\":\"78e1cf5a-f0b7-4b09-93ce-39ae05fa4c19\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"confirmPrice\",\"uuid\":\"f497165d-d794-4406-b8a0-d34af237c410\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"confirmTime\",\"uuid\":\"9f6f3497-1b23-4899-a23d-491eb5d63619\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"timeInterval\",\"uuid\":\"d016e874-86b1-4b38-a32c-695ebf1014fa\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"isUp\",\"uuid\":\"07208ee5-8de1-473c-ae06-865b4d8a12c4\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"pivotPhysicalTime\",\"uuid\":\"873488a6-6190-4226-b6b0-3ba26a236688\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"confirmPhysicalTime\",\"uuid\":\"1d62daf3-0d63-4281-90c2-354013313f13\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"6d9681bf-d005-43e1-95b7-daf9e7295b88\",\"name\":\"Price\",\"columns\":[{\"name\":\"price\",\"uuid\":\"8ab7cc9b-8678-4e9d-802a-d05315a0476f\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"real\",\"uuid\":\"ffb63fea-9e3a-40ab-a3c3-9f569f74ba37\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"physicalTime\",\"uuid\":\"f82e8247-56ec-43f3-9260-92ce3246e8d4\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"time\",\"uuid\":\"f1c52130-57e8-43fa-88f6-cfc01cc0123d\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"13536269-5965-4afd-8232-845b579808a1\",\"name\":\"Channel\",\"columns\":[{\"name\":\"startTime\",\"uuid\":\"eeacbbe8-cec4-4e6a-86d0-69e8488ff444\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"endTime\",\"uuid\":\"01e26af9-9b15-4e46-ad69-b0e83ac3282b\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"topStartPrice\",\"uuid\":\"52a1767c-1f25-416e-a2d5-a9c8d7d0162e\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"topEndPrice\",\"uuid\":\"52023e62-b696-49e1-9c64-59edcaecc3df\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"centerStartPrice\",\"uuid\":\"1f34cb7c-5797-4d12-869b-a7aeb6676c8c\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"centerEndPrice\",\"uuid\":\"e3369221-3b22-405a-ba22-025563ba0ffe\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"bottomStartPrice\",\"uuid\":\"681d502b-7ad0-4ed3-87d1-5680f8bc5126\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"bottomEndPrice\",\"uuid\":\"1be05d25-0762-43d0-a9d3-35067635c757\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"slope\",\"uuid\":\"741c2c6c-18d3-4569-a45a-b68d872dfa5a\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"startPhysicalTime\",\"uuid\":\"20d1d5b5-386b-4044-a3d9-c7bce3e8dbcb\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"endPhysicalTime\",\"uuid\":\"6dd0bb31-97e6-44d3-9e89-bcad3fb11f67\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"318d0774-d2af-4801-b71b-a79a668b86c6\",\"name\":\"ProbabilityPoint\",\"columns\":[{\"name\":\"time\",\"uuid\":\"c5d17ab8-dd0c-4ca2-8e24-2adc42049ded\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"price\",\"uuid\":\"f3a1f73e-c3ee-461a-b033-176832c8f6de\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"648c4570-5d1f-4cbf-829c-f0d9e3516366\",\"name\":\"ProbabilityInfo\",\"columns\":[{\"name\":\"time\",\"uuid\":\"0bdae203-1787-46f1-b6ed-2939141703df\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"type\",\"uuid\":\"bbbd6176-c47e-4d40-8a8d-b2b1ff9d7be7\",\"type\":\"BYTE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"positive\",\"uuid\":\"7a78de36-85b8-46a0-86ad-22b92de2661e\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"probabilitiesCount\",\"uuid\":\"cca1cd2f-d5be-41b9-a991-709745eaf485\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"probabilitiesIndex\",\"uuid\":\"9a32411e-3f43-405f-9ca5-5abae71646bb\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"84c11d87-2519-4895-b34a-a5360889f621\",\"name\":\"TriggerExpectedValue\",\"columns\":[{\"name\":\"negValue\",\"uuid\":\"70b74c6a-5bb1-4725-82a3-4115cf1b6ac4\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"posValue\",\"uuid\":\"5bb92c9a-5518-4435-b452-686aad92606e\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"time\",\"uuid\":\"cfb8673d-8ed6-4f20-95b1-4c11bd7b4bb3\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"negSwingProjection\",\"uuid\":\"c9a3bc9c-2108-4155-a230-a68dfbed229e\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"posSwingProjection\",\"uuid\":\"a7ee65e8-22ff-4bbf-870a-182a5c1ace9d\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"0273e857-d040-48e4-979a-4e5080953773\",\"name\":\"InterpPriceProb\",\"columns\":[{\"name\":\"time\",\"uuid\":\"80e9a018-31b1-46be-a7c5-8f4c9b9f0388\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"price\",\"uuid\":\"d2f9c075-e98f-44ab-8d49-86e69328cb69\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"8813bf9b-344e-4b1b-8daf-440dca2ef2e1\",\"name\":\"ChannelInfo\",\"columns\":[{\"name\":\"time\",\"uuid\":\"21d8356f-1c0c-4c46-8e52-3d7e45acdd05\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"slope\",\"uuid\":\"3db27543-1d04-45a7-8592-7967056b78d3\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"maxslope\",\"uuid\":\"3849d275-2410-455b-8d09-c2276f2b3442\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"minslope\",\"uuid\":\"f61fa8fc-f14d-47f4-a14c-512208e0bdd2\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"maxwidth\",\"uuid\":\"af183629-3898-49ec-b5bd-4b2c6f290d1c\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"minwidth\",\"uuid\":\"e3824e82-4171-4330-8dde-1100fb9b8937\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"width\",\"uuid\":\"9a7970c2-01b7-4bc5-96ab-7f23950529bb\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"maxprice\",\"uuid\":\"6f6f610f-5085-433d-b668-e8198528eb47\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"minprice\",\"uuid\":\"a4061e85-d894-4852-b9b0-902117246083\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"tickslength\",\"uuid\":\"059252ad-4394-420a-8aed-a2e4dc60a970\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"lwratio\",\"uuid\":\"7f40b607-a017-45ff-aa1f-452410f4ecdd\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"tickspivot\",\"uuid\":\"35fac5bf-1be9-4f32-9daa-98e2dba2e89b\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"pricepivot\",\"uuid\":\"f975b0df-7d66-41e6-a9fa-08aa1f438339\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"ticksth\",\"uuid\":\"432023d5-d3f7-472a-b71d-fbe7bafac6ec\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"priceth\",\"uuid\":\"9b58e619-9ab9-4f20-b03e-726c31899042\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"goingup\",\"uuid\":\"81096ad7-7300-4785-956c-51f78c707c80\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"HHPrice\",\"uuid\":\"d3c0c246-bf74-4df8-a2d4-f413abc2ae12\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"LLPrice\",\"uuid\":\"7c43f8cc-eef7-4c81-b95f-33c709f0187e\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"HHTime\",\"uuid\":\"708a52ec-68a4-4c1c-9882-a671a7373590\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"LLTime\",\"uuid\":\"f6e87f90-909c-40e0-96e7-ae1616b02a1a\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"pearsonr\",\"uuid\":\"77c0b2fd-fae2-49f4-b0cd-25e5f010a710\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"fe259a4b-572f-4564-a992-4b0f40255024\",\"name\":\"Probability\",\"columns\":[{\"name\":\"time\",\"uuid\":\"7a7948df-ee59-4292-aea7-65748a0e14f5\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"posPrice\",\"uuid\":\"da0a6f2e-bbdf-4ca6-919c-905111986e92\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"negPrice\",\"uuid\":\"d832d140-96d4-49af-acd4-71ec931ec8c0\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"posTradeDirection\",\"uuid\":\"1fa03292-ea83-4690-a149-ff746337d2d5\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"1f92e2bc-e710-4953-a811-4e60c1eab897\",\"name\":\"ProbabilityPercent\",\"columns\":[{\"name\":\"posTHPrice\",\"uuid\":\"05aa762b-6c4b-4839-bd2a-55fa734e3dca\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"negTHPrice\",\"uuid\":\"daffad05-ac75-459c-b4fb-32bb2df5f638\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"posCurrentPrice\",\"uuid\":\"2fcff986-02a0-4ae6-9e3d-f85399dd8123\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"negCurrentPrice\",\"uuid\":\"3ead80b8-2c1b-4ca9-992f-0c308c57620e\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"time\",\"uuid\":\"f2c38869-4631-4461-b742-ba429823efe7\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"posTradeDirection\",\"uuid\":\"79ed25ff-3063-41a4-b758-f60127c2471a\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"c01dc06d-a6ee-43c3-8a21-45d936c6fa27\",\"name\":\"Trade\",\"columns\":[{\"name\":\"openTime\",\"uuid\":\"8fe3097f-d987-461b-a046-1aea1ee7bba5\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"openPrice\",\"uuid\":\"a8b710e7-5ce1-4501-98db-a651a3a0e322\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"closeTime\",\"uuid\":\"939d8494-a8b7-448e-856c-fc57ba2f86ef\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"closePrice\",\"uuid\":\"90a3bce7-39ad-4d74-8f02-77175d105d91\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"isGain\",\"uuid\":\"511c83a3-3834-42c8-9ae7-1a445daa03d0\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"isClosed\",\"uuid\":\"3f25afe5-2aeb-47f6-8099-1bff7b2e1385\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"isLong\",\"uuid\":\"efd30e1e-c4ef-46e5-b972-f01bef2f2bbf\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"orderId\",\"uuid\":\"546faa30-6a89-4457-8c69-eb14ef737b9d\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"eventTime\",\"uuid\":\"a51d8891-d4d3-47e3-b68b-3bcf52ce1f5e\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"openingCount\",\"uuid\":\"73a7e5e5-2b46-4bb1-ad4d-48bbb68e2d2d\",\"type\":\"BYTE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"opening0\",\"uuid\":\"ba65648a-7e14-403e-a56c-039e4f256d8e\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"opening1\",\"uuid\":\"fca1f914-b3a1-456e-8902-2f22c798ef14\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"openPhysicalTime\",\"uuid\":\"5b236825-3174-4299-8479-c05a460c34f2\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"closePhysicalTime\",\"uuid\":\"72910a94-5e27-4c2e-8cf7-7b1ea7fcb825\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"eventPhysicalTime\",\"uuid\":\"b714bf41-36a9-4075-9faf-3170156fed1d\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"opening0_childType\",\"uuid\":\"ef02d751-2b00-4314-991e-37eccc865e93\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"opening1_childType\",\"uuid\":\"ce0df907-052c-4c0f-a174-5428b22ec9a3\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"opening0_orderId\",\"uuid\":\"25e82b10-3784-4eb7-b3fb-869fa4a0a3ca\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"opening1_orderId\",\"uuid\":\"0cbffa73-28d9-422e-8ee6-c57a89446588\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"42d87236-c196-4133-9d71-08eef05bdc79\",\"name\":\"Equity\",\"columns\":[{\"name\":\"total\",\"uuid\":\"7ae10c70-77b4-4e6e-9eaf-ce58a4714655\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"totalPrice\",\"uuid\":\"2645f402-eede-4d4a-a74c-0eeec6dab231\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"fakeTime\",\"uuid\":\"59924417-ca93-4a38-a8ae-274f6332497e\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]}]}";
 	
 	static {
 		SIGNATURES = new HashMap<>();
		SIGNATURES.put("1b11d903-f4b0-4aa5-b5d0-3665d45163e3", "4488c076-8b2e-492b-99ba-8a9ffa73eed3 LONG; 19fb83f6-3ed5-4ff2-b281-4f7aab13937d DOUBLE; 123e327d-14ca-4a02-a14e-f51b752d87d3 DOUBLE; 284c7bba-8bc7-490b-9fca-ca30cff93b4d DOUBLE; 0745ee9c-7d4c-4e62-9102-1c498bedbe9c LONG; 7fbbbc88-8201-4583-a6ec-439bbc80be70 LONG; b4233eec-23c6-4880-8747-14fb2d3efd59 LONG; ffec2584-3796-43c5-8ac8-e05c091fa567 LONG; ");
		SIGNATURES.put("3999c52c-e8c2-450b-8ac7-fd297d84e419", "c850cfc3-e382-4309-92c7-daee7917d67a DOUBLE; 78e1cf5a-f0b7-4b09-93ce-39ae05fa4c19 LONG; f497165d-d794-4406-b8a0-d34af237c410 DOUBLE; 9f6f3497-1b23-4899-a23d-491eb5d63619 LONG; d016e874-86b1-4b38-a32c-695ebf1014fa LONG; 07208ee5-8de1-473c-ae06-865b4d8a12c4 BOOLEAN; 873488a6-6190-4226-b6b0-3ba26a236688 LONG; 1d62daf3-0d63-4281-90c2-354013313f13 LONG; ");
		SIGNATURES.put("6d9681bf-d005-43e1-95b7-daf9e7295b88", "8ab7cc9b-8678-4e9d-802a-d05315a0476f DOUBLE; ffb63fea-9e3a-40ab-a3c3-9f569f74ba37 BOOLEAN; f82e8247-56ec-43f3-9260-92ce3246e8d4 LONG; f1c52130-57e8-43fa-88f6-cfc01cc0123d LONG; ");
		SIGNATURES.put("13536269-5965-4afd-8232-845b579808a1", "eeacbbe8-cec4-4e6a-86d0-69e8488ff444 LONG; 01e26af9-9b15-4e46-ad69-b0e83ac3282b LONG; 52a1767c-1f25-416e-a2d5-a9c8d7d0162e DOUBLE; 52023e62-b696-49e1-9c64-59edcaecc3df DOUBLE; 1f34cb7c-5797-4d12-869b-a7aeb6676c8c DOUBLE; e3369221-3b22-405a-ba22-025563ba0ffe DOUBLE; 681d502b-7ad0-4ed3-87d1-5680f8bc5126 DOUBLE; 1be05d25-0762-43d0-a9d3-35067635c757 DOUBLE; 741c2c6c-18d3-4569-a45a-b68d872dfa5a BOOLEAN; 20d1d5b5-386b-4044-a3d9-c7bce3e8dbcb LONG; 6dd0bb31-97e6-44d3-9e89-bcad3fb11f67 LONG; ");
		SIGNATURES.put("318d0774-d2af-4801-b71b-a79a668b86c6", "c5d17ab8-dd0c-4ca2-8e24-2adc42049ded LONG; f3a1f73e-c3ee-461a-b033-176832c8f6de DOUBLE; ");
		SIGNATURES.put("648c4570-5d1f-4cbf-829c-f0d9e3516366", "0bdae203-1787-46f1-b6ed-2939141703df LONG; bbbd6176-c47e-4d40-8a8d-b2b1ff9d7be7 BYTE; 7a78de36-85b8-46a0-86ad-22b92de2661e BOOLEAN; cca1cd2f-d5be-41b9-a991-709745eaf485 INTEGER; 9a32411e-3f43-405f-9ca5-5abae71646bb INTEGER; ");
		SIGNATURES.put("84c11d87-2519-4895-b34a-a5360889f621", "70b74c6a-5bb1-4725-82a3-4115cf1b6ac4 DOUBLE; 5bb92c9a-5518-4435-b452-686aad92606e DOUBLE; cfb8673d-8ed6-4f20-95b1-4c11bd7b4bb3 LONG; c9a3bc9c-2108-4155-a230-a68dfbed229e DOUBLE; a7ee65e8-22ff-4bbf-870a-182a5c1ace9d DOUBLE; ");
		SIGNATURES.put("0273e857-d040-48e4-979a-4e5080953773", "80e9a018-31b1-46be-a7c5-8f4c9b9f0388 LONG; d2f9c075-e98f-44ab-8d49-86e69328cb69 DOUBLE; ");
		SIGNATURES.put("8813bf9b-344e-4b1b-8daf-440dca2ef2e1", "21d8356f-1c0c-4c46-8e52-3d7e45acdd05 LONG; 3db27543-1d04-45a7-8592-7967056b78d3 DOUBLE; 3849d275-2410-455b-8d09-c2276f2b3442 DOUBLE; f61fa8fc-f14d-47f4-a14c-512208e0bdd2 DOUBLE; af183629-3898-49ec-b5bd-4b2c6f290d1c DOUBLE; e3824e82-4171-4330-8dde-1100fb9b8937 DOUBLE; 9a7970c2-01b7-4bc5-96ab-7f23950529bb DOUBLE; 6f6f610f-5085-433d-b668-e8198528eb47 DOUBLE; a4061e85-d894-4852-b9b0-902117246083 DOUBLE; 059252ad-4394-420a-8aed-a2e4dc60a970 INTEGER; 7f40b607-a017-45ff-aa1f-452410f4ecdd DOUBLE; 35fac5bf-1be9-4f32-9daa-98e2dba2e89b INTEGER; f975b0df-7d66-41e6-a9fa-08aa1f438339 DOUBLE; 432023d5-d3f7-472a-b71d-fbe7bafac6ec INTEGER; 9b58e619-9ab9-4f20-b03e-726c31899042 DOUBLE; 81096ad7-7300-4785-956c-51f78c707c80 BOOLEAN; d3c0c246-bf74-4df8-a2d4-f413abc2ae12 DOUBLE; 7c43f8cc-eef7-4c81-b95f-33c709f0187e DOUBLE; 708a52ec-68a4-4c1c-9882-a671a7373590 LONG; f6e87f90-909c-40e0-96e7-ae1616b02a1a LONG; 77c0b2fd-fae2-49f4-b0cd-25e5f010a710 DOUBLE; ");
		SIGNATURES.put("fe259a4b-572f-4564-a992-4b0f40255024", "7a7948df-ee59-4292-aea7-65748a0e14f5 LONG; da0a6f2e-bbdf-4ca6-919c-905111986e92 DOUBLE; d832d140-96d4-49af-acd4-71ec931ec8c0 DOUBLE; 1fa03292-ea83-4690-a149-ff746337d2d5 BOOLEAN; ");
		SIGNATURES.put("1f92e2bc-e710-4953-a811-4e60c1eab897", "05aa762b-6c4b-4839-bd2a-55fa734e3dca LONG; daffad05-ac75-459c-b4fb-32bb2df5f638 LONG; 2fcff986-02a0-4ae6-9e3d-f85399dd8123 LONG; 3ead80b8-2c1b-4ca9-992f-0c308c57620e LONG; f2c38869-4631-4461-b742-ba429823efe7 LONG; 79ed25ff-3063-41a4-b758-f60127c2471a BOOLEAN; ");
		SIGNATURES.put("c01dc06d-a6ee-43c3-8a21-45d936c6fa27", "8fe3097f-d987-461b-a046-1aea1ee7bba5 LONG; a8b710e7-5ce1-4501-98db-a651a3a0e322 DOUBLE; 939d8494-a8b7-448e-856c-fc57ba2f86ef LONG; 90a3bce7-39ad-4d74-8f02-77175d105d91 DOUBLE; 511c83a3-3834-42c8-9ae7-1a445daa03d0 BOOLEAN; 3f25afe5-2aeb-47f6-8099-1bff7b2e1385 BOOLEAN; efd30e1e-c4ef-46e5-b972-f01bef2f2bbf BOOLEAN; 546faa30-6a89-4457-8c69-eb14ef737b9d INTEGER; a51d8891-d4d3-47e3-b68b-3bcf52ce1f5e LONG; 73a7e5e5-2b46-4bb1-ad4d-48bbb68e2d2d BYTE; ba65648a-7e14-403e-a56c-039e4f256d8e LONG; fca1f914-b3a1-456e-8902-2f22c798ef14 LONG; 5b236825-3174-4299-8479-c05a460c34f2 LONG; 72910a94-5e27-4c2e-8cf7-7b1ea7fcb825 LONG; b714bf41-36a9-4075-9faf-3170156fed1d LONG; ef02d751-2b00-4314-991e-37eccc865e93 INTEGER; ce0df907-052c-4c0f-a174-5428b22ec9a3 INTEGER; 25e82b10-3784-4eb7-b3fb-869fa4a0a3ca INTEGER; 0cbffa73-28d9-422e-8ee6-c57a89446588 INTEGER; ");
		SIGNATURES.put("42d87236-c196-4133-9d71-08eef05bdc79", "7ae10c70-77b4-4e6e-9eaf-ce58a4714655 DOUBLE; 2645f402-eede-4d4a-a74c-0eeec6dab231 DOUBLE; 59924417-ca93-4a38-a8ae-274f6332497e LONG; ");
 	}
 	
	public BaseChartMDBSession(String sessionName, File root) throws IOException {
		super(sessionName, root, SIGNATURES, SCHEMA_JSON);
	}
	
	public BaseChartMDBSession(String sessionName, File root, SessionMode mode) throws IOException {
		super(sessionName, root, mode, SIGNATURES, SCHEMA_JSON);
	}

/* BEGIN USER SESSION CODE */
	/* The user can write his code here */
	/* END USER SESSION CODE */	

	private BandsMDB internal_connectTo_BandsMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (BandsMDB) _cache.get(file);
			}
			BandsMDB mdb = new BandsMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "1b11d903-f4b0-4aa5-b5d0-3665d45163e3");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public BandsMDB connectTo_BandsMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_BandsMDB(getFile(filename), bufferSize);
	}
	
	public BandsMDB connectTo_BandsMDB(String filename) throws IOException {
		return connectTo_BandsMDB(filename, 100);
	}

	private PivotMDB internal_connectTo_PivotMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (PivotMDB) _cache.get(file);
			}
			PivotMDB mdb = new PivotMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "3999c52c-e8c2-450b-8ac7-fd297d84e419");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public PivotMDB connectTo_PivotMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_PivotMDB(getFile(filename), bufferSize);
	}
	
	public PivotMDB connectTo_PivotMDB(String filename) throws IOException {
		return connectTo_PivotMDB(filename, 100);
	}

	private PriceMDB internal_connectTo_PriceMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (PriceMDB) _cache.get(file);
			}
			PriceMDB mdb = new PriceMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "6d9681bf-d005-43e1-95b7-daf9e7295b88");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public PriceMDB connectTo_PriceMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_PriceMDB(getFile(filename), bufferSize);
	}
	
	public PriceMDB connectTo_PriceMDB(String filename) throws IOException {
		return connectTo_PriceMDB(filename, 100);
	}

	private ChannelMDB internal_connectTo_ChannelMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (ChannelMDB) _cache.get(file);
			}
			ChannelMDB mdb = new ChannelMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "13536269-5965-4afd-8232-845b579808a1");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public ChannelMDB connectTo_ChannelMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_ChannelMDB(getFile(filename), bufferSize);
	}
	
	public ChannelMDB connectTo_ChannelMDB(String filename) throws IOException {
		return connectTo_ChannelMDB(filename, 100);
	}

	private ProbabilityPointMDB internal_connectTo_ProbabilityPointMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (ProbabilityPointMDB) _cache.get(file);
			}
			ProbabilityPointMDB mdb = new ProbabilityPointMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "318d0774-d2af-4801-b71b-a79a668b86c6");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public ProbabilityPointMDB connectTo_ProbabilityPointMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_ProbabilityPointMDB(getFile(filename), bufferSize);
	}
	
	public ProbabilityPointMDB connectTo_ProbabilityPointMDB(String filename) throws IOException {
		return connectTo_ProbabilityPointMDB(filename, 100);
	}

	private ProbabilityInfoMDB internal_connectTo_ProbabilityInfoMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (ProbabilityInfoMDB) _cache.get(file);
			}
			ProbabilityInfoMDB mdb = new ProbabilityInfoMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "648c4570-5d1f-4cbf-829c-f0d9e3516366");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public ProbabilityInfoMDB connectTo_ProbabilityInfoMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_ProbabilityInfoMDB(getFile(filename), bufferSize);
	}
	
	public ProbabilityInfoMDB connectTo_ProbabilityInfoMDB(String filename) throws IOException {
		return connectTo_ProbabilityInfoMDB(filename, 100);
	}

	private TriggerExpectedValueMDB internal_connectTo_TriggerExpectedValueMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (TriggerExpectedValueMDB) _cache.get(file);
			}
			TriggerExpectedValueMDB mdb = new TriggerExpectedValueMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "84c11d87-2519-4895-b34a-a5360889f621");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public TriggerExpectedValueMDB connectTo_TriggerExpectedValueMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_TriggerExpectedValueMDB(getFile(filename), bufferSize);
	}
	
	public TriggerExpectedValueMDB connectTo_TriggerExpectedValueMDB(String filename) throws IOException {
		return connectTo_TriggerExpectedValueMDB(filename, 100);
	}

	private InterpPriceProbMDB internal_connectTo_InterpPriceProbMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (InterpPriceProbMDB) _cache.get(file);
			}
			InterpPriceProbMDB mdb = new InterpPriceProbMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "0273e857-d040-48e4-979a-4e5080953773");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public InterpPriceProbMDB connectTo_InterpPriceProbMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_InterpPriceProbMDB(getFile(filename), bufferSize);
	}
	
	public InterpPriceProbMDB connectTo_InterpPriceProbMDB(String filename) throws IOException {
		return connectTo_InterpPriceProbMDB(filename, 100);
	}

	private ChannelInfoMDB internal_connectTo_ChannelInfoMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (ChannelInfoMDB) _cache.get(file);
			}
			ChannelInfoMDB mdb = new ChannelInfoMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "8813bf9b-344e-4b1b-8daf-440dca2ef2e1");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public ChannelInfoMDB connectTo_ChannelInfoMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_ChannelInfoMDB(getFile(filename), bufferSize);
	}
	
	public ChannelInfoMDB connectTo_ChannelInfoMDB(String filename) throws IOException {
		return connectTo_ChannelInfoMDB(filename, 100);
	}

	private ProbabilityMDB internal_connectTo_ProbabilityMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (ProbabilityMDB) _cache.get(file);
			}
			ProbabilityMDB mdb = new ProbabilityMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "fe259a4b-572f-4564-a992-4b0f40255024");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public ProbabilityMDB connectTo_ProbabilityMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_ProbabilityMDB(getFile(filename), bufferSize);
	}
	
	public ProbabilityMDB connectTo_ProbabilityMDB(String filename) throws IOException {
		return connectTo_ProbabilityMDB(filename, 100);
	}

	private ProbabilityPercentMDB internal_connectTo_ProbabilityPercentMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (ProbabilityPercentMDB) _cache.get(file);
			}
			ProbabilityPercentMDB mdb = new ProbabilityPercentMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "1f92e2bc-e710-4953-a811-4e60c1eab897");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public ProbabilityPercentMDB connectTo_ProbabilityPercentMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_ProbabilityPercentMDB(getFile(filename), bufferSize);
	}
	
	public ProbabilityPercentMDB connectTo_ProbabilityPercentMDB(String filename) throws IOException {
		return connectTo_ProbabilityPercentMDB(filename, 100);
	}

	private TradeMDB internal_connectTo_TradeMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (TradeMDB) _cache.get(file);
			}
			TradeMDB mdb = new TradeMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "c01dc06d-a6ee-43c3-8a21-45d936c6fa27");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public TradeMDB connectTo_TradeMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_TradeMDB(getFile(filename), bufferSize);
	}
	
	public TradeMDB connectTo_TradeMDB(String filename) throws IOException {
		return connectTo_TradeMDB(filename, 100);
	}

	private EquityMDB internal_connectTo_EquityMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (EquityMDB) _cache.get(file);
			}
			EquityMDB mdb = new EquityMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "42d87236-c196-4133-9d71-08eef05bdc79");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public EquityMDB connectTo_EquityMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_EquityMDB(getFile(filename), bufferSize);
	}
	
	public EquityMDB connectTo_EquityMDB(String filename) throws IOException {
		return connectTo_EquityMDB(filename, 100);
	}
	private long modificationToken = 0;

	public void modified() {
		modificationToken++;
	}

	public long getModificatonToken() {
		return modificationToken;
	}
}

