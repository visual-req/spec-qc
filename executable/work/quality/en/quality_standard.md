## Quality Standards (Built-in)

### Applicable Industries
- Applicable industry: General (cross-industry)
- Industry boundary: Industry-specific rules should be placed in the corresponding industry quality files. If the industry is not specified, only general rules are used by default to avoid cross-industry false positives.

### Requirement Analysis “Common Mistakes” Rule List

#### 1. Using “etc.” in lists causes missing items
- Category: Ambiguous wording
- Description: Using “etc.” can cause missing items
- Bad example: Using “etc.” to indicate a list
- Recommendation: Enumerate all options and describe them as an explicit list
- Benefit: Clear expression and consistent understanding
- When to use: Describing an enumerable, finite set
- Related quality dimension: Precision

#### 2. “Random” is ambiguous and may lead to incorrect implementation
- Description: “Random” is ambiguous and may lead to incorrect implementation results
- Recommendation: Specify the exact random algorithm, probability, etc.
- Benefit: Clear algorithm definition enables consistent understanding for dev and QA
- When to use: Describing general algorithms

#### 3. “Highlight” is not a concrete color value
- Description: “Highlight” is not a concrete color value and may result in arbitrary, inconsistent colors
- Recommendation: Specify the exact RGB values used under each condition
- Benefit: Clear RGB definition for all cases
- When to use: When the same data needs different colors under different conditions (e.g., show yellow/red/green based on remaining time)

#### 4. “Fuzzy search” is ambiguous without specifying the matching type
- Description: There are multiple fuzzy matching types (prefix / suffix / contains). The type must be specified.
- Bad example: Using “fuzzy search” without defining the matching mechanism
- Recommendation: Specify which fuzzy type to use, or define “fuzzy search” clearly in common rules
- Benefit: Clear and unambiguous understanding of fuzzy matching
- When to use: Describing query matching rules

#### 5. Copywriting causes user misunderstanding
- Description: Copywriting leads to user misunderstanding
- Bad example: “Are you sure to cancel?”  Confirm | Cancel
- Recommendation: Use explicit wording
- Recommendation example: “You are about to cancel. The entered data will become invalid. Continue?”  Confirm | Cancel
- Benefit: Clear wording reduces misunderstanding
- When to use: UI copywriting

#### 6. “Ascending/descending” is clearer than “forward/reverse”
- Description: “Forward/reverse” can be misunderstood
- Recommendation: Use “ascending/descending” instead of “forward/reverse”
- Benefit: Consistent understanding
- When to use: Sorting

#### 7. Ambiguous numbering rule leads to wrong implementation
- Description: Ambiguous numbering rules cause implementation errors
- Bad example: “Use ABC-234 format”
- Recommendation: Use a table to define each part of the identifier and explain each part
- Benefit: Clear expression and correct implementation; avoids inconsistent understanding
- When to use: Generating identifiers

#### 8. UI control is unnamed, causing comprehension difficulty
- Description: Missing a proper control name makes the requirement hard to understand
- Bad example: “(?) control” (unclear what control it is)
- Recommendation: Assign IDs/names to controls and refer to them explicitly
- Benefit: Clear expression and correct implementation; avoids inconsistent understanding
- When to use: Pages with many controls or composite controls

#### 9. Threshold/range boundary is unclear (>, <=, inclusive/exclusive)
- Description: Natural language like “over/within/before/after” often misses inclusivity (=), units/definition, and reference point, causing ambiguity. This occurs in time, quantity, amount, distance, age, count, etc.
- Bad example: “Over 18 can register” (is 18 included?); “Amount not over 100” (is 100 included?); “Within 3km free delivery” (is 3km included?); “Over x days” (missing reference point and whether natural days/24h)
- Recommendation: Use testable boundary expressions: comparison operators (>=, <, <=, >), open/closed intervals ([ ] / ( )), units and definition. For time ranges, define T0 (field/event), time zone, and day-cut. Provide 2–3 boundary examples (hit / not hit).
- Recommendation example: age ∈ [18, 60); amount <= 100.00 (precision=2); distance < 3.0 km; validity = [T0, T0+5d] with T0=payment_success_time (Asia/Shanghai, day cut 00:00)
- Benefit: Improves precision and testability; reduces disputes and rework
- When to use: Any rule involving thresholds/ranges/comparisons/windows/validity/limits

#### 10. Time boundary is unclear (after/before/within/over)
- Category: Ambiguous wording
- Keywords: time boundary / open-closed interval
- Description: For time rules, “after/before/within/over” is ambiguous about inclusivity, reference point, time zone and day-cut, causing implementation and acceptance mismatch.
- Bad example: “after”, “before”
- Recommendation: (1) Decide semantics first: if it means operation order (e.g., “after clicking, navigate”), treat it as a flow order issue and specify trigger/sequence. (2) If it is a time rule, define T0 (field/event), inclusivity ([ ]/( ) or >=/>), time zone and day-cut, and give 2–3 examples.
- Recommendation example: Use >= for inclusive; use ()/[] to show open/closed interval
- Benefit: Avoid boundary ambiguity and production disputes; reduce rework and test omissions
- When to use: Time windows, validity, deadlines, grace periods, statistical definitions
- Related quality dimension: Precision

#### 11. Time semantics is unclear (“this week”)
- Category: Ambiguous wording
- Keywords: time definition / week definition
- Description: “This week” may start on Monday or Sunday by locale/system and may depend on time zone/day-cut; unclear definition causes inconsistent stats/filtering.
- Bad example: “this week”
- Recommendation: Define week start day, time zone, day-cut, and calculation method; provide a testable time-range expression.
- Recommendation example: The week containing the current date: Monday–Sunday
- Benefit: Ensures consistent definition for stats/filters/reports; reduces cross-client disputes
- When to use: Filters, reports, periodic tasks, settlement cycles
- Related quality dimension: Precision

#### 12. Relative time window is unclear (“within one month/5 days/one year”)
- Category: Ambiguous wording
- Keywords: relative time / reference point
- Description: Relative windows like “within one month/5 days/one year/last 7 days/last 3 months” need T0, unit definition (natural day/24h/business day), month-calendar vs fixed days, and inclusivity. Note: “default sort by policy created time desc” is sorting, not a time window, and should not be flagged here.
- Bad example: “within one month”, “5 days”, “one year”, “last 7 days”, “last 3 months”
- Recommendation: Define as T0 (field/event) + offset (unit/definition) + interval boundary (>=/>/[ ]/( )) + time zone/day-cut; add cross-month/leap-year examples when needed.
- Recommendation example: window = [T0-7d, T0], T0=created_time, unit=natural day, TZ=Asia/Shanghai; deadline = T0+5d (include T0, exclude end), clarify using [ ]/( )
- Benefit: Avoids ambiguity; improves consistency and test reproducibility
- When to use: Validity, sign-up/approval deadlines, grace periods, cooling-off periods, membership expiration
- Related quality dimension: Precision

#### 13. Missing requirement scenarios/modules
- Category: Requirement omission
- Description: Missing key usage scenarios or key functional modules causes a large missing scope (not just missing field details). Note: “state–action combinations incomplete” should be checked via decision matrix rules, not this module-omission rule.
- Recommendation: Use closed-loop thinking: walk through the main flow and branches from the user perspective; list involved modules (pages/APIs/tasks/reports/notifications/permissions) and cross-check; use user journey/object lifecycle if needed.
- Benefit: Avoid major functional gaps; reduce rework and launch risk
- When to use: Business scenario descriptions, flow-based requirements, cross-module linkage
- Related quality dimension: Completeness

#### 13.1 Missing requirement details (fields/constraints/definitions)
- Category: Requirement omission
- Description: Main flow may be complete, but key details are missing (field definitions, ranges, validation, exceptions, definitions, boundaries, permissions, messages), causing inconsistent implementation or untestable acceptance.
- Bad example: “Support exporting report” (no fields/format/sort/filter/time definition); “Support filtering” (no defaults/empty/combination limits); “Sort by policy created time desc” (no tie-break/pagination stability); “Auto reminder” (no trigger/target/template/channel/frequency)
- Recommendation: Complete the detail checklist: triggers & state machine; data dictionary (type/unit/precision/enums); validations (required/range/regex/unique); defaults & sorting; errors & error codes; permission matrix; message/reminder list (target/template/channel/frequency/unsubscribe); sample data and I/O examples; boundaries and tolerance.
- Benefit: Improves acceptability and consistency; reduces repeated clarification during integration
- When to use: List/query/export/compute/validate/permission/notify/message/external API requirements
- Related quality dimension: Completeness, Precision

#### 14. Missing reverse flow leads to repeated clarification during dev/test
- Description: Missing reverse flow causes repeated confirmation of details during development and testing
- Bad example: Card insertion flow misses the case of card damage during processing
- Recommendation: Scan each node in the flowchart to identify reverse flows
- Benefit: Ensures reverse flows are sufficiently analyzed and fully covered
- When to use: Any flow should be checked for reverse flows

#### 15. Missing action constraints under certain states leads to production issues
- Description: Missing action constraints in certain states delays issues to production
- Bad example: Incomplete mapping between states and homepage actions/fields (e.g., draft/pending review/published/offline missing coverage)
- Recommendation: Use a decision matrix (state x action/field/visibility/button) to ensure 100% coverage, with testable results and exception handling for each combination
- Benefit: Decision matrix ensures full coverage
- When to use: Any stateful system should analyze state-action constraint combinations

#### 16. Missing roles causes missing stakeholders and flawed logic
- Description: Missing roles causes missing stakeholders, flawed logic, failures and downstream issues
- Bad example: Defining a new role name that does not exist besides standard roles
- Recommendation: Build a role list and walk scenarios to identify all stakeholders
- Benefit: Clear role analysis reveals hidden features via role-based end-to-end walkthrough
- When to use: For larger/newer requirements, perform stakeholder identification before deep analysis

#### 17. Missing key roles (experts/consultants/ops/reviewers, etc.)
- Category: Role omission
- Keywords: stakeholder / role list
- Description: Missing key roles breaks closed-loop flows, incomplete permission design, invalid scenarios, and deviates from real business.
- Bad example: Personal finance app missing financial advisors; design becomes unreasonable
- Recommendation: List roles based on business objects and full flows (internal/external/system roles). For each role, define tasks, entry points, and permission boundaries; validate each key node has an owner via scenario walkthrough.
- Recommendation example: Roles: user/financial advisor/customer support/ops/risk/reviewer/system jobs; define visible data scope and allowed actions per role
- Benefit: Reduce rework and post-launch “add roles/permissions”; improve executability and compliance
- When to use: Cross-department, multi-role processes, scenarios needing review/risk/support
- Related quality dimension: Completeness

#### 18. Missing functions cause wrong estimation and incomplete system
- Description: Missing functions cause wrong schedule estimation and delays; the system cannot deliver business value
- Bad example: Forgetting to specify real-time saving of personalization settings
- Recommendation: Use object journey to identify missing functions
- Benefit: Better estimation and impact analysis
- When to use: For new requirements, use object journey and user journey walkthrough

#### 19. Incomplete user journey leads to missing implicit features
- Category: Feature omission
- Keywords: user journey / implicit features
- Description: Incomplete user journey review misses implicit actions such as acquisition/engagement/touchpoints
- Bad example: Missing growth step for “new user sign-up gift”
- Recommendation: Walk the full journey “touch → enter → sign up → first order/first use → retention → recall”; enumerate inputs/outputs/triggers/capabilities; make implicit features explicit (growth tracking, notifications, benefits, risk controls)
- Recommendation example: After signup: issue coupons, send messages, report analytics; first-login guide; invite flow; landing-page attribution parameter pass-through
- Benefit: Reduce omissions and rework; align implementation with ops goals; lower post-launch patch risk
- When to use: Features with acquisition/conversion/retention goals
- Related quality dimension: Completeness

#### 20. Incomplete object journey causes missing key object handling (contracts/agreements, etc.)
- Category: Feature omission
- Keywords: object journey / key object omission
- Description: Core business objects are not fully modeled/covered, causing missing required handling (contracts/agreements/attachments/reconciliation, etc.)
- Bad example: Acquiring system missing contract handling
- Recommendation: Use object journey / object lifecycle: list core objects (order/contract/payment/invoice/settlement, etc.) and cover “create → transfer → query → change → archive/void”; cover relationships and external dependencies.
- Recommendation example: Object=contract: generation rules, signing entry, signing status, archive/download; relation with order/payment; retry on signing failure; permissions and audit
- Benefit: Avoid delays and rework; ensure complete delivery scope; reduce late discovery during integration
- When to use: Systems with strong business objects, especially cross-system flows or compliance needs
- Related quality dimension: Completeness

#### 21. Number display format and decimal precision are not defined
- Description: Display format and decimal precision calculation method are not defined
- Recommendation: Define UI detail standards
- Recommendation example: Right-align numbers; keep 2 decimals for amounts; rounding rule (half-up)
- Benefit: Unifies UI and interaction details and avoids inconsistent compromises
- When to use: Any numeric/date fields requiring formatting

#### 22. Missing description of data loading for controls on page load
- Description: Some controls require backend-loaded data at page load. Missing the loading description causes the control to be forgotten.
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Enumerate all UI controls and specify their loading behaviors

#### 23. Dropdown missing “empty/clear” option
- Description: Missing an empty option makes it impossible to clear after selection
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Dropdowns and similar controls should confirm an empty option strategy

#### 24. Missing action details
- Description: Missing action details
- Bad example: A composite picker (dropdown + transfer list). Switching dropdown affects transfer list, which must be specified.
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: For composite controls, specify mutual influences and interactions under conditions

#### 25. Missing action decomposition causes strange behaviors
- Description: Missing decomposition causes strange behavior for combined actions
- Bad example: A button press includes press/hold/release; improper handling causes issues
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: For detailed operations, specify action decomposition and combinations clearly

#### 26. Missing animation details leads to deviation from expected effect
- Description: Missing animation details causes deviation from expected effect and hurts UX
- Bad example: Lottery wheel rotation effect not specified
- Recommendation: Define animation parameters: direction/distance, rotation direction/angle, duration, easing/acceleration
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: UI includes animations

#### 27. Missing responsive rules causes chaotic responsive layouts
- Description: Missing responsive rules causes layout chaos
- Bad example: Not specifying how the page should look on different devices
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Responsive development

#### 28. Missing layout rules causes unfriendly display in some conditions
- Description: Missing layout rules causes unfriendly display in some conditions
- Bad example: If a panel section can be hidden, should the rest shift left or keep position leaving blank space?
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Layout changes based on conditions

#### 29. Missing external integration discovered only during integration testing
- Description: Discovering external system dependencies only during integration delays delivery
- Recommendation: Use object journey to identify external dependencies
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Requirements with external dependencies

#### 30. Missing combinations of conditions leads to processing defects
- Description: Missing condition combinations causes defects
- Bad example: Approval expiration case
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Recommendation example: Use a decision matrix to cover all condition combinations
- Benefit: Ensure 100% coverage of condition combinations
- When to use: Multi-factor conditions

#### 31. No emergency plan leads to inability to stop loss
- Description: Without an emergency plan, the system cannot stop loss in time
- Bad example: A newly launched system lost large money due to loopholes without a kill switch
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Recommendation example: Use risk identification to find potential emergency risks
- Benefit: Enables stop-loss actions under emergency risk
- When to use: Allow one-click disable/unpublish of risky modules

#### 32. Missing recovery measures for contention scenarios causes business stuck
- Description: Missing recovery measures for contention leads to business interruption
- Bad example: Refund failed but system recorded refunded; cannot refund again
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Recommendation example: Use exception-oriented thinking to find failure cases and remedies
- Benefit: Allows business to continue after exceptions
- When to use: Handling continuation after business exceptions

#### 33. Missing validation lets bad data flow downstream and cause bugs
- Description: Missing validation allows bad data to propagate and cause bugs
- Recommendation: Use a validation matrix and unify table headers (aligned with this standard, per language): Field/Required/Format/Length/Range/Unique/Others
- Recommendation example: zh-CN: | 字段 | 必填 | 格式 | 长度 | 范围 | 唯一 | 其他 |; en: | Field | Required | Format | Length | Range | Unique | Others |; ja: | フィールド | 必須 | 形式 | 長さ | 範囲 | 一意 | その他 |
- Benefit: Ensures validation is not omitted
- When to use: UI submission validation

#### 34. Missing permissions leads to permission chaos and over-privilege defects
- Category: Permission omission
- Keywords: RBAC / org permission / data permission / state permission
- Description: Missing permissions causes permission chaos and over-privilege defects
- Bad example: Unclear permissions lead to confusion and privilege escalation
- Recommendation: Use a permission matrix system: control-level RBAC + org structure permission + data permission + state permission (actionability matrix), including entry permissions and conditions.
- Recommendation example: Role-permission matrix for roles; similar for org permission; data-permission matrix; decision matrix for state permission
- Benefit: Avoid permission chaos and over-privilege caused by omissions
- When to use: When defining permissions
- Related quality dimension: Completeness

#### 35. Missing diversity of entities causes major requirement gaps
- Description: Missing diversity of entities causes major omissions and missing scenarios
- Bad example: Diversity of employee identities; diversity of warehouse types
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Ensures diverse scenarios are considered and avoids major omissions
- When to use: Confirm diversity with stakeholders via guided questions

#### 36. Missing diversity of business types may make some business unsupported
- Description: Missing diversity of business types may make one or more business types unsupported
- Bad example: Warehouse could be self-operated or rental/managed
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Ask stakeholders about diversity of business types

#### 37. Missing diversity of operations causes missing functions at launch
- Description: Missing operation diversity leads to missing functions and unmet business needs
- Bad example: Multiple login methods
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Review non-standard operations (add/delete/query/detail/edit/select/export)

#### 38. Missing combinations of operations causes missing combos and bugs
- Description: Operation combinations are omitted, causing missing combos and real-world bugs
- Bad example: Multiple keys can be pressed (e.g., Ctrl+S). Need mutual exclusion rules and behaviors.
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: High-interaction scenarios (games, drawing apps, soft keyboard, etc.)

#### 39. Multiple terminals access the same system; different terminals behave differently
- Description: Missing listing of terminals leads to missing terminal implementation and failed acceptance
- Bad example: Meeting room booking supports QR, Bluetooth, card, remote; must list all
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Systems accessed by multiple terminals; consider access methods and requirements

#### 40. Different browsers
- Description: Access by different browsers
- Bad example: Different browser engines/versions
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Multi-terminal access; consider browser diversity

#### 41. Different screen sizes
- Description: Different screen sizes
- Bad example: Screen size differences
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Multi-terminal access; consider screen size diversity

#### 42. Inventory upper limit
- Description: Inventory upper limit
- Bad example: Lottery oversold due to missing limit, causing overselling
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Avoid hidden business loopholes
- When to use: For hidden upper/lower limits, think from the sponsor’s perspective about hidden boundaries

#### 43. Budget upper limit
- Description: Budget upper limit
- Bad example: Promotion over-issued due to logic errors
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: When similar risk exists during analysis/design

#### 44. Numeric type upper limit
- Description: Numeric type upper limit
- Bad example: SSD failure due to forgetting Integer.Max
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Avoid bugs caused by technical limits
- When to use: When numbers may overflow

#### 45. Quantity boundaries (including 0/negative/huge values)
- Category: Boundary omission
- Keywords: quantity upper/lower / zero / negative
- Description: If min/max/boundary rules (0 allowed? negative allowed? decimals allowed?) are missing, it may cause loopholes or system errors.
- Bad example: Qty not limited → 999999; qty=0 → empty order; negative qty breaks inventory
- Recommendation: Define for every quantity field: min/max/step (int/decimal), unit, inclusivity, user message on overflow and backend guardrails; apply to import/batch APIs too.
- Recommendation example: qty is integer, 1<=qty<=999; overflow returns error code and “quantity range 1–999”; import validates per row and outputs failure details
- Benefit: Prevent abuse/overflow/data pollution; improve stability and controllability
- When to use: Quantity/count/inventory change/tickets/points, etc.
- Related quality dimension: Precision

#### 46. Distance/range boundaries (delivery/location/geofence)
- Category: Boundary omission
- Keywords: distance boundaries / range definition
- Description: Missing bounds/units/definition for distance/range leads to wrong billing, unreachable orders, or performance problems.
- Bad example: Delivery radius unbounded; mixed units (m/km) causes billing errors
- Recommendation: Define unit (m/km), definition (straight-line/path), min/max and rounding; define overflow handling (prompt/block/degrade to manual confirmation).
- Recommendation example: delivery_radius_km 0<r<=30; distance_km rounded to 1 decimal; >30km cannot order and show message
- Benefit: Avoid unreachable business and billing disputes; improve UX and operability
- When to use: Delivery, service radius, check-in, geofence, mileage billing
- Related quality dimension: Precision

#### 47. Age boundaries (minors/compliance)
- Category: Boundary omission
- Keywords: age min/max / birthday definition
- Description: Missing age calculation definition and boundary rules (birthday/natural day/time zone) causes compliance risk and inconsistent decisions.
- Bad example: No restriction for minors; “18th birthday inclusive?” mismatch causes disputes
- Recommendation: Define how to compute age: which field is birthday, time zone, inclusivity; define allowed range and exception handling (missing/unparseable).
- Recommendation example: Compliance uses “has reached 18th birthday (inclusive)”; missing birthday blocks with prompt to complete
- Benefit: Reduce compliance risk; ensure consistent and testable decisions
- When to use: Registration, content rating, payment, exams, minor protection
- Related quality dimension: Precision

#### 48. Network bandwidth upper limit
- Description: Network bandwidth upper limit
- Bad example: Bulk downloads saturate bandwidth and break connectivity
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Centralized server downloads

#### 49. Disk capacity upper limit
- Description: Disk capacity upper limit
- Bad example: Disk full and cannot store data due to missing capacity limit
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Large data storage

#### 50. Missing export details causes usability issues for exported reports
- Description: Missing details makes exported reports hard to read/use (format/sort/filter/limits)
- Recommendation: Specify all details: format, sorting, query conditions, row limits
- Benefit: Ensures sufficiently detailed and accurate information
- When to use: Exporting reports

#### 51. Missing necessary details causes missing implementation of operations
- Description: Missing key details causes some operations to be omitted; when it happens, actual behavior is unpredictable
- Bad example: Bar chart lacks color scheme, number display, Y-axis rules
- Recommendation: Enumerate each sub-control and specify details
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Composite controls

#### 52. Missing analytics tracking (“events”) removes usage data basis
- Description: Missing tracking prevents usage statistics and removes data basis for experience improvement
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Ensures tracking coverage for data collection and analysis
- When to use: ToC user behavior analytics / growth (events/funnel/conversion/retention). For internal enterprise systems or most ToB systems, do not treat as mandatory by default.

#### 53. Missing sorting rule causes order mismatch vs expectation
- Description: Missing sorting condition makes data order undefined and inconsistent with expectation
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Querying data

#### 54. Missing “industry conventions” causes unusable implementation
- Description: Missing description of industry conventions leads to assumptions; the implemented system becomes unusable
- Bad example: Address naming variations known within industry but not described
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Makes hidden requirements explicit; improves completeness
- When to use: New industries/new rules

#### 55. Missing terminology definitions causes misunderstanding and ambiguity
- Description: Missing term definitions causes difficulty or ambiguity
- Recommendation: When terms appear, verify they are defined in a glossary/term list
- Benefit: Align understanding and wording; improve communication efficiency
- When to use: Always

#### 56. Missing impact scenarios causes exceptions and bugs
- Description: Missing impact scenarios causes exceptions and bugs
- Bad example: Ctrl+C/V across different apps requires complex rules
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Avoid missing scenarios and ensure quality
- When to use: Seemingly simple but logically complex behaviors; use impact analysis

#### 57. Missing validation for outbound data causes wrong data leakage
- Description: Missing outbound data validation allows wrong data to be exposed externally
- Recommendation: Define outbound validation methods
- Benefit: Avoid providing wrong data to users
- When to use: Export reports, send notifications/emails

#### 58. Missing error handling makes errors unhandled and recurring
- Description: Missing error handling leaves errors unaddressed and recurring
- Recommendation: Define batch error handling: retries, notifications, etc.
- Benefit: Avoid silent failures without follow-up handling
- When to use: Batch processing

#### 59. Define handling for upload errors (prompts/notifications)
- Description: Define handling for upload errors (prompts/notifications)
- Recommendation: Define handling for upload errors (prompts/notifications)
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: File uploads

#### 60. Define handling for email sending errors (prompt/notify/log)
- Description: Define handling for email sending errors (prompt/notify/log)
- Recommendation: Define handling for email sending errors (prompt/notify/log)
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Email/SMS notification sending

#### 61. Redundant states/contents
- Category: Redundant handling/content
- Description: Redundant states cause confusion and messy logic, resulting in weird bugs
- Recommendation: Name states in past tense to avoid duplication; use flowcharts; use “spotlight method” to deduplicate states
- Benefit: Avoid confusion caused by duplicated states
- When to use: State modeling
- Related quality dimension: Correctness

#### 62. Decision matrix explosion makes it hard to fill/read/understand/maintain
- Description: An exploded decision matrix is hard to fill/read/understand/maintain
- Recommendation: Separate mutually exclusive conditions
- Recommendation example: Decouple; split logic by processing stage; split matrix by an attribute
- Benefit: Shrinks matrix size for easier filling/reading/maintenance
- When to use: Using decision matrices
- Related quality dimension: Readability

#### 63. Hard-to-read formatting
- Category: Hard-to-read formatting
- Description: Deep nested indentation is hard to write/read and easy to miss; increases complexity
- Bad example: Hard to write/maintain/read
- Recommendation: Replace deep nesting with expression trees
- Recommendation example: Use decision matrices/flowcharts; split conditions into multiple decision matrices
- Benefit: Avoid overly nested documents and maintenance difficulty
- When to use: When “deep indentation” appears
- Related quality dimension: Readability

#### 64. Obscure wording makes writing and understanding difficult
- Description: Obscure, verbose sentences are hard to craft and hard to understand; changes are painful
- Bad example: “When current date and … apply date … approval date … old expiry … new expiry …”
- Recommendation: Use symbols/placeholders
- Benefit: Clearer understanding, easier writing, easier maintenance on change
- When to use: When sentences become complex

#### 65. Using tables but still having long sentences inside the table
- Description: Tables are used but long sentences still exist
- Bad example: Same as above
- Recommendation: Further split the table, or extract long sentences and reference them
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: When long sentences appear in tables

#### 66. Provide sample data
- Description: Provide sample data
- Bad example: Provide sample data
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Slightly complex logic that needs examples

#### 67. Hard to adjust wording; hard to understand; hard to maintain
- Description: Hard to adjust wording; hard to understand; hard to maintain
- Bad example: Same as above
- Recommendation: Split long sentences using an attribute table
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: When sentences become complex

#### 68. Missing supporting materials (templates)
- Category: Missing materials
- Description: Missing templates makes template-related features impossible to develop
- Bad example: Missing required templates
- Recommendation: Use scanning methods (including flow diversity analysis) to identify needed templates and request them from the business side
- Benefit: Ensure materials are provided in time for better analysis
- When to use: Templates for import/export and similar scenarios
- Related quality dimension: Completeness

#### 69. Missing notification copy makes notification implementation impossible
- Description: Missing notification copy prevents implementing notifications as required
- Bad example: Missing notification copy
- Recommendation: Ask the business side to confirm copy or provide notification templates
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Sending notifications

#### 70. Missing contract-related handling creates legal risks
- Description: Missing contract-related handling creates potential legal risks
- Bad example: Missing required contracts/agreements/legal documents
- Recommendation: Identify needed contracts via scanning methods (flowchart, diversity, etc.) and request them
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Handling contracts/agreements

#### 71. Inconsistent with facts
- Category: Inconsistent with facts
- Description: Factually incorrect requirements cause wrong code and logic, resulting in bugs
- Recommendation: Separate scheduled jobs from the flowchart
- Benefit: Avoid fact-inconsistent errors
- When to use: Flowcharts involving timing requirements
- Related quality dimension: Correctness

#### 72. Incorrect processing logic
- Category: Incorrect processing logic
- Description: Passing an incorrect understanding to dev/QA without confirmation causes wrong implementation
- Recommendation: Use reverse confirmation to verify logic matches business intent
- Benefit: Align processing logic with business requirements
- When to use: After requirement writing
- Related quality dimension: Correctness

#### 73. Improper authentication method causes identity leakage/over-privilege
- Description: Improper authentication causes identity leakage and over-privilege
- Recommendation: Two-factor authentication
- Benefit: Ensures authentication is adequately protected; establishes unified authentication
- When to use: All authentication-related flows

#### 74. Wrong data model
- Category: Data model error
- Description: Wrong data model makes all subsequent business logic descriptions wrong
- Bad example: Push–pull model
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Avoid complexity/poor UX/production bugs caused by wrong models
- When to use: Modeling
- Related quality dimension: Correctness

#### 75. Errors due to misunderstanding business rules
- Description: Errors due to misunderstanding business rules
- Bad example: “Split red packet” should be real-time split, not pre-split
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: When actual usage may exceed expected boundary; when users may not use features as assumed

#### 76. Wrong calculation rule description causes implementation bugs
- Description: Wrong calculation rule description causes wrong implementation and bugs
- Bad example: Homework grading: wrong rule between first and second submission, causing an unexpected third batch
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Rules requiring calculation

#### 77. Missing proper accounting method creates business loopholes
- Description: Without a suitable accounting method, business loopholes appear
- Bad example: Double-entry bookkeeping
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Financial accounting, warehouse management, asset-disposal systems

#### 78. Typos in formulas
- Category: Typo
- Description: Wrong math formulas cause misunderstanding and wrong code results
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Avoid misunderstanding and quality issues caused by typos
- When to use: Describing mathematical formulas
- Related quality dimension: Correctness

#### 79. Misspellings cause wrong understanding
- Description: Misspellings cause wrong understanding; typos may be directly copied into implementation
- Recommendation: Use spell check and common typo list
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Always

#### 80. Not following standards
- Category: Not following standards
- Description: Not following existing industry standards causes wrong implementation and unmet requirements
- Bad example: Writing custom email validation; not following international phone number rules causes SMS failure
- Recommendation: Prefer industry/international/national standards (RFC/ISO/GB, etc.). Provide links, scope, boundaries and examples; if deviating, explain why and compatibility strategy.
- Recommendation example: Phone/international phone uses E.164; email uses RFC 5322 subset; datetime uses ISO 8601; unify money precision and rounding
- Benefit: Avoid mismatch between ad-hoc rules and comprehensive standards
- When to use: Validations and rule-based processing; many widely used technologies have formal standards
- Related quality dimension: Correctness

#### 81. Using the wrong modeling tool
- Category: Wrong tool
- Description: Confused tooling reduces readability and correctness; wrong tools may express wrong results
- Bad example: Using flowcharts for all logic
- Recommendation: Choose suitable tools: decision matrix, expression tree, etc.
- Benefit: Right tool improves correctness, readability, ease of writing, and change responsiveness
- When to use: Requirement writing; consult scenario-to-tool mapping
- Related quality dimension: Correctness, Readability, Consistency

#### 82. Same type of thing described with different tools
- Description: The same type of thing is described with different tools (e.g., containment can be expressed by mind map/fishbone/tree)
- Bad example: Mixing mind maps, fishbone, and tree directories for containment
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Agree on tools for expressing a class of things

#### 83. Two exits cause confusion
- Description: Two exits cause confusion
- Bad example: Same as above
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Drawing flowcharts

#### 84. Custom symbols (triangles/trapezoids) violate conventions
- Description: Custom symbols violate industry conventions
- Bad example: Same as above
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: When similar risk exists during analysis/design

#### 85. Missing logging
- Category: Missing logging
- Description: Without logs, errors cannot be located or solved
- Recommendation: For exports, define logs including time/content/file path/status
- Benefit: Enables audit and troubleshooting via logs
- When to use: Exporting reports
- Related quality dimension: Completeness, Correctness

#### 86. Batch processing should have clear logs
- Description: Batch jobs should log run time, processed count, and result status
- Recommendation: Same as above
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Batch processing

#### 87. File upload should have clear logs
- Description: Upload should log upload time, original file, validation result
- Recommendation: Also log parsing results, etc.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: File uploads

#### 88. Email sending should have clear logs
- Description: Email sending should log send time and result
- Recommendation: Same as above
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Email sending

#### 89. Easily missed handling
- Category: Easily missed handling
- Description: Missing handling for easily missed operations hurts UX and may create wrong data
- Recommendation: Define page refresh behavior in common handling
- Benefit: Covers possible operations to ensure UX
- When to use: Web applications should describe this
- Related quality dimension: Completeness, Reusability

#### 90. Define behavior when forwarding/sharing
- Description: Define behavior when forwarding/sharing
- Recommendation: Same as above
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: When similar risk exists during analysis/design

#### 91. Define behavior when opening favorite/bookmark links
- Description: Define behavior when opening favorites/bookmarks
- Recommendation: Same as above
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: When similar risk exists during analysis/design

#### 92. Define behavior on double-clicking a button
- Description: Define behavior on double-click
- Recommendation: Same as above
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: When similar risk exists during analysis/design

#### 93. Offline handling should be explicit (retry/network check/offline support)
- Description: Offline handling should include retry (optional), network check (optional), and …
- Recommendation: Offline handling should include retry (optional), network check (optional), and offline support (optional)
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Mobile exam system can preload papers to mitigate disconnection

#### 94. Slow loading: show skeleton first, then progress after x seconds
- Description: If data loads slowly, show skeleton first; if still not loaded after x seconds, show progress
- Recommendation: Show a progress bar after x seconds
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Reduce user anxiety when loading is slow

#### 95. Define concurrency handling when two users operate the same record
- Description: Define concurrency handling when two users operate the same record
- Recommendation: Same as above
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Most systems need this scenario

#### 96. Duplicated descriptions
- Category: Duplicated descriptions
- Description: Duplicate wording creates redundancy; changes are painful and omissions likely
- Recommendation: Use reusable control descriptions to reduce duplication
- Recommendation example: Reusable control example
- Benefit: Easier reading and stable conventions; avoids repeated document maintenance
- When to use: Frequently used components should be documented as reusable components
- Related quality dimension: Reusability

#### 97. Use “common handling” to reduce duplication
- Description: Use common handling to reduce duplication
- Recommendation: Same as above
- Recommendation example: Common handling example
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Global, one-time descriptions should be put into common handling

#### 98. Use abstraction to reduce duplication
- Description: Use abstraction to reduce duplication
- Recommendation: Same as above
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: When multiple flows share strong commonality

#### 99. Use a message list to reduce duplication
- Description: Use a message list to reduce duplication
- Recommendation: Same as above
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: When the same message format is repeated; for multilingual support, put all messages into a message list

#### 100. Underestimated complexity
- Category: Underestimated complexity
- Description: Complexity is not estimated or severely underestimated, causing long run time, user impatience, or “freeze” symptoms
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: Large-data processing; consider performance for the scenario’s boundaries

#### 101. Too many selected objects in drawing software causes performance drop
- Description: Selecting too many objects causes severe performance degradation
- Bad example: Same as above
- Recommendation: Add explicit rules/boundaries and testable examples and error handling to avoid ambiguity.
- Benefit: Reduce ambiguity/omissions and improve correctness and acceptance consistency
- When to use: When similar risk exists during analysis/design

#### 102. Ambiguous wording: “formula / calculation definition”
- Category: Ambiguous wording
- English 1: ambiguous
- English 2: formula
- Keywords: “formula / calculation definition”
- Description: Mentions “formula/calculate by formula/definition”, but does not provide an executable, testable formula, or misses key elements, causing implementation/acceptance mismatch (variables, unit, boundary, rounding, precision, defaults).
- Bad example: (1) “Score is calculated by the following formula” but no formula provided; (2) total = unit_price * qty (missing tax/discount/coupon/shipping; missing rounding and precision)
- Recommendation: Define precisely with “formula table + definition notes + sample I/O”: complete expression; variable definitions/units/source fields; scope; inclusivity; precision and rounding (round/floor/ceil); exceptions/defaults (null/0/negative); consistency (front/back)
- Recommendation example: total_amount = round((unit_price * qty) - discount + shipping, 2); unit=CNY; qty integer>=1; discount>=0; return error code on failure; 2–3 sample I/O; if requirement says “sort by policy submit date desc”, define field=policy_submit_date and order=desc
- Benefit: Avoid ambiguity; improve correctness and test reproducibility
- When to use: Money/billing/scoring/ratio/allocation/stat definition calculations
- Related quality dimension: Precision

#### 103. Poor architecture choice: “async/MQ/event”
- Category: Poor architecture choice
- English 2: mq
- Keywords: “async/MQ/event”
- Description: Scenarios that clearly need async decoupling/peak shaving/reliable delivery/eventual consistency but have no MQ/event design, leading to long sync chains, hard recovery, duplicate execution on retry, or reconciliation issues
- Bad example: After payment trigger inventory/coupons/notifications/stats/external callbacks, but all done synchronously in one transaction or serial sync calls
- Recommendation: Identify actions to eventize: message list (topic/queue, producer, consumer, trigger, payload schema); consistency (outbox/transactional message); idempotency key; retry/DLQ; monitoring/alerts; compensation/replay entry
- Recommendation example: OrderPaid event; producer=order-service; consumers=inventory/coupon/notification; idempotency_key=order_id; retry=exponential backoff; DLQ=orderpaid.dlq; provide replay admin page
- Benefit: Lower coupling and blast radius; improve throughput and recoverability; reduce integration/regression risk
- When to use: Async notifications, cross-system integration, long tasks, bulk processing, state-machine side effects
- Related quality dimension: Completeness

### Requirement Quality Issue Types and Solutions (Supplement)

#### 104. Ambiguous wording: unclear or ambiguous expression
- Category: Ambiguous wording
- Keywords: “etc/random/highlight/fuzzy search/forward-reverse sort/ID rule/control naming/over X days”
- Description: Unclear/ambiguous expression leads to inconsistent implementation and acceptance
- Bad example: “etc.” in lists; “random” algorithms; “highlight” colors; “fuzzy search” without matching rules; “Are you sure to cancel?”; “forward/reverse” sorting; “ABC-234 format”; unnamed control; “over 5 days”
- Recommendation: Enumerate options; define random algorithm/probabilities; define RGB; specify fuzzy type; improve copy; use ascending/descending; table for ID parts; control numbering; use >=/<=/intervals or T+x/D+x
- Benefit: Reduce ambiguity and rework; improve testability
- When to use: All rules/algorithms/queries/sorts/IDs/copy/range descriptions
- Related quality dimension: Precision

#### 105. Requirement omission: missing scenarios
- Category: Requirement omission
- Keywords: “scenario/closed-loop/journey/use case”
- Description: Missing key usage scenarios or business closed-loop causes major functional gaps. Note: role omissions should be handled by role rules, not this one.
- Bad example: Missing contract handling; missing growth step in signup gift; missing partial refund/exchange scenarios
- Recommendation: Walk full user journey; use use-case list for main/branch/exception paths; use object journey/lifecycle; check roles/permissions separately
- Benefit: Avoid major gaps; lower launch risk
- When to use: Scenario review, flow walkthrough, closed-loop checks
- Related quality dimension: Completeness

#### 106. Requirement omission: missing flows
- Category: Requirement omission
- Keywords: “flowchart/state-action/decision matrix/reverse flow”
- Description: Missing main or reverse flows, or incomplete state-action combinations, causing repeated clarification
- Bad example: Card insertion flow misses reverse case; incomplete state-action coverage
- Recommendation: Scan each node for reverse flows; use decision matrix for state-action combinations to reach 100% coverage
- Benefit: Reduce omissions and rework; improve testability
- When to use: Flow-based or state-machine businesses
- Related quality dimension: Completeness

#### 107. Requirement omission: missing details
- Category: Requirement omission
- Keywords: “format/loading/control/interaction/responsive/export/tracking/industry rules”
- Description: Missing UI/interaction/export/tracking/industry-convention details leads to implementation/UX deviation or acceptance disputes
- Bad example: Number format undefined; missing control-loading rules; dropdown no empty option; composite interaction undefined; animation vague; responsive rules missing; export format/sort missing; tracking missing; industry rules missing
- Recommendation: Define UI standards; enumerate controls and loading; empty/all option strategy; define composite linkage; define animation params; define responsive layout rules; define export format/sort/filters; include analytics in NFR; collect industry conventions
- Benefit: Reduce rework and disputes; improve delivery consistency
- When to use: UI design, export, interaction, non-functional requirements
- Related quality dimension: Completeness

#### 108. Requirement omission: missing permissions
- Category: Requirement omission
- Keywords: “RBAC/data scope/least privilege/over-privilege/masking”
- Description: Insufficient permission analysis (roles/functions/data scope/field-level) leads to over-privilege, leakage, failed audits, and risk
- Bad example: “Admin can operate” without role list/actions; support sees full contact/ID; missing field visibility and export permission
- Recommendation: Define boundaries with permission matrices: RBAC (role x action) + data-permission (role x scope/object/field); masking/decryption conditions; approval chain; audit logs; error prompts
- Benefit: Lower risk; improve compliance/auditability/operability
- When to use: Admin consoles, support/ops/review UIs, sensitive data query/export, permission changes
- Related quality dimension: Completeness

#### 108.1 Requirement omission: missing external dependencies/integrations
- Category: Requirement omission
- Keywords: “dependency/integration/reconciliation/scheduled jobs/time zone/contract”
- Description: Missing dependency analysis (systems/contracts/ownership/time zone/day cut) causes integration failure, inconsistency, or scheduled job errors
- Bad example: Depends on reconciliation/clearing system without schema/authority; third-party SMS/email without retry/degrade; scheduled jobs ignore time zones
- Recommendation: Use dependency list and contracts: system owners, API/file schema, auth/ratelimit, timeout/retry/idempotency, error codes/degrade, reconciliation definition; for periodic jobs define time zone/cutoff/backfill/replay strategy
- Benefit: Lower integration risk; improve delivery certainty and operability
- When to use: Cross-system integration, reconciliation/settlement, notification channels, batch/scheduled jobs, external data sources
- Related quality dimension: Completeness

#### 109. Requirement omission: missing boundaries/exceptions
- Category: Requirement omission
- Keywords: “upper/lower/oversell/over-issue/retry/failure prompt/concurrency/hotkeys”
- Description: Missing boundary and exception strategies causes incidents, bad data, and poor UX
- Bad example: Oversell; promotion over-issue; numeric overflow; bandwidth bottleneck; batch error without retry; upload failure without prompt; Ctrl+C/V rules missing across apps
- Recommendation: Analyze hidden bounds from sponsor view; assess technical limits; enumerate/matrix complex scenario impacts; define error handling for batch/upload/notifications (retry/idempotency/degrade/prompt)
- Benefit: Reduce incidents and improve robustness
- When to use: Concurrency/resources/batch/files/notifications
- Related quality dimension: Robustness

#### 110. Insufficient stakeholder identification
- Category: Requirement omission
- Keywords: “stakeholder/org/approval chain/ops/compliance”
- Description: Missing key stakeholders causes missing permissions/flows/ops/compliance requirements
- Bad example: OA attendance approval only considers employee+HR, missing IT/manager
- Recommendation: Use organization stakeholder map; map goals/constraints to flow/permissions/acceptance items
- Benefit: Reduce rework and conflicts; improve feasibility
- When to use: Enterprise systems, approval flows, cross-department collaboration
- Related quality dimension: Completeness

#### 111. Missing symmetric (forward/reverse) business flows
- Category: Requirement omission
- Keywords: “symmetric flow/reverse flow/return/cancel/reversal”
- Description: Only forward flow is covered; missing reverse/symmetric flow breaks closed-loop
- Bad example: Shopping app covers ordering but not returning
- Recommendation: Identify symmetric flows and use flowchart/state machine to define permissions/data/log definitions
- Benefit: Ensures closed-loop delivery; reduces post-launch patches
- When to use: Order/payment/approval/execution flows with cancel/rollback
- Related quality dimension: Completeness

#### 112. Redundant handling/content: complex and hard-to-maintain descriptions
- Category: Redundant handling/content
- Keywords: “duplicate states/matrix explosion/long text/long sentences in table”
- Description: Redundant states, long text, and matrix explosion make it hard to understand and maintain. Note: If the requirement does not even describe states, do not flag this as “redundant”; missing states belong to omission/modeling rules.
- Bad example: Duplicated state names; matrix explosion; complex logic described by pure text; nested long sentences; long sentences in tables
- Recommendation: Name states in past tense and deduplicate via flowcharts; split exclusive conditions/parameters; replace long text with expression trees/decision matrices/flowcharts; use symbols; split tables and add sample data
- Benefit: Better readability and maintainability; fewer omissions
- When to use: Complex conditions, complex flows, rule-engine requirements
- Related quality dimension: Maintainability

#### 113. Expression/format issues: non-standard formatting and mixed tools
- Category: Expression/format
- Keywords: “typos/formula parentheses/industry standard/UI standard/flowchart standard”
- Description: Non-standard format or obscure expression causes misreading and implementation deviations
- Bad example: Typos; missing/wrong parentheses; validation not following standards; no UI standard; non-standard flowchart symbols
- Recommendation: Spell check; define validations by standards; import and update UI standards; unify tools and follow flowchart standards
- Benefit: Reduce misunderstandings and rework; improve standardization
- When to use: Document reviews, interaction and rule definitions
- Related quality dimension: Standardization

#### 114. Logic/data-model errors: inconsistent with business requirements
- Category: Logic/data-model error
- Keywords: “auth/modeling/algorithm/counting/accounting”
- Description: Logic or model inconsistent with business requirements makes subsequent implementation/acceptance wrong
- Bad example: Weak authentication (single password); wrong model; wrong algorithm/count rules; no double-entry bookkeeping; formula typos
- Recommendation: Use 2FA/unified auth; correct modeling and scenario validation; define algorithm/count rules with samples; follow industry definition for money/warehouse; verify formulas
- Benefit: Avoid fatal errors and large-scale rework
- When to use: Auth, billing/counting/finance/core algorithms
- Related quality dimension: Correctness

#### 115. Wrong use of description tools
- Category: Wrong tool usage
- Keywords: “flowchart/mind map/fishbone/tree/symbol standard”
- Description: Inappropriate/mixed tools make structure unclear and non-standard
- Bad example: Flowchart for everything; mixing tools for same relation; non-standard symbols
- Recommendation: Choose tools per scenario; standardize tools for same type; follow standard symbols and conventions
- Benefit: Better consistency and reusability
- When to use: Structured/visual requirement expression
- Related quality dimension: Standardization

#### 116. Missing logging for auditability
- Category: Missing logging
- Keywords: “export/batch/upload/notification/send result/audit”
- Description: For auditable scenarios (compliance/security/privacy/money/config changes), missing logs makes tracing impossible and fails audits. Do not force this for low-risk queries unless audit is required.
- Bad example: Export without time/content/status; batch without runtime/count/result; upload without validation/parsing status; email without send result; sensitive data query without access logs/audit fields
- Recommendation: Only for auditable objects/operations, define a logging matrix: time, operator/role, object/data scope, conditions, result, failure reason, trace id, rule version. For sensitive access, add reason code/ticket/auth basis. Define retention/access control; immutable if needed.
- Benefit: Better traceability and troubleshooting efficiency
- When to use: Money/compliance/privacy/security/config change/export/download/batch/integration; low-risk queries only when audit required
- Related quality dimension: Traceability

#### 117. Edge cases easily missed: refresh/offline/multi-device concurrency
- Category: Easily missed handling
- Keywords: “refresh/offline/retry/offline-mode/conflict/loading feedback/anti-double-submit/share/bookmark”
- Description: For high-frequency/real-time/strong-interaction pages, missing refresh/offline/concurrency handling causes data loss/conflicts/poor UX. Do not force on low-frequency pure-display flows.
- Bad example: Refresh loses filters; offline edits lose content without retry/offline hint; multi-device switches cause conflicts; rapid clicks cause duplicates; slow load has no feedback
- Recommendation: Only enable for high-frequency/real-time scenarios. Define in common handling: refresh/back retention strategy, offline retry/offline mode, conflict handling (lock/version/prompt), anti-double-submit, slow-load feedback (skeleton/timeout progress)
- Benefit: Better UX and stability; fewer complaints
- When to use: Real-time/high-frequency/strong-interaction web/mobile flows
- Related quality dimension: Robustness

#### 118. Duplicate descriptions: repeated controls/flows/copy increase maintenance cost
- Category: Duplicate descriptions
- Keywords: “common controls/common handling/message list/multilingual”
- Description: Large-scale duplication creates redundancy and omission risk when changing; small phrase-level repetition is usually not flagged unless it creates conflicts.
- Bad example: Repeating full dialogs/messages/queries; repeating same flow with slight differences; repeating validation rules; multilingual copy list duplicated
- Recommendation: Extract common controls and handling; abstract shared flow; put messages into a message list with multilingual support
- Benefit: Lower maintenance cost; better consistency and reusability
- When to use: Global interactions, common components, message copy and multilingual
- Related quality dimension: Reusability

#### 119. Phone/email change lacks security closed-loop, enabling account takeover
- Category: Security risk omission
- Keywords: “phone/email/rebind/recovery/impersonation/notification”
- Description: If phone/email is used as login factor, notification channel or recovery credential, missing rebind/unbind/recovery verification and risk controls allows attackers to take over accounts via SMS/email hijack or social engineering.
- Bad example: Allow change phone/email with only login session (no re-verify/cool-down/notification); password reset relies only on SMS code; allow large transfers immediately after rebind
- Recommendation: In common handling, define rules for “rebind/recovery/notification/audit trail”
  - Rebind verification: prerequisites (valid session, recent strong auth); rebind must trigger strong auth (at least 2FA or equivalent, e.g., old channel verification + biometrics/payment password/ID check); high-risk cases require dual control/manual review
  - Cool-down and limits: cool-down/delayed effect after rebind; restrict critical actions (limit increase/withdraw/transfer/add payee) or force step-up; define exceptions and approvals
  - Notification and reconciliation: notify at least two channels among “old channel + new channel + in-app” (time/location/device/type/appeal entry); define handling and retry on notification failure
  - Anti-attack and recovery: retry/lock/anti-bombing; anti-replay (one-time, TTL, transaction_id binding); account recovery and appeals; integrate with risk engine (score/blacklist/anomalous geo/device)
  - Audit trail: log trigger reason, verification method, result, risk score, device/IP, masked old/new values, rule version and approval chain
- Benefit: Reduce takeover risk, loss, complaints; improve audit pass rate
- Applicability/strictness: Mainly for ToC systems. For internal ToB systems where phone/email is only a notification channel and there is no self-service rebind/recovery/funds operation, downgrade to a reminder.
- Strict recommendations (ToC): Strong auth for rebind/recovery; cool-down with high-risk action restrictions; notifications to old channel + in-app with one-click stop-loss (freeze/change password/appeal); anti-bombing/anti-replay/lock on failure; end-to-end traceability
- When to use: ToC signup/login/recovery/change phone/email/unbind third-party/security setting changes; ToB when open external access or self-service rebind/recovery/funds exists
- Related quality dimension: Security, Traceability

#### 120. Device change/new device login lacks handling rules, enabling theft
- Category: Security risk omission
- Keywords: “new device/device fingerprint/trusted device/session kick/anomalous login”
- Description: In multi-device or device-change scenarios, missing new-device detection, trusted device binding, session governance, and anomalous login handling enables attackers to log in from new devices and perform sensitive actions.
- Bad example: New device login treated same as old device (no step-up/notification); old device remains valid forever; anomalous geo/device still allows withdraw/transfer
- Recommendation: In common handling, define “new device detection + step-up + session governance + notification”
  - Device detection: define device fingerprint fields and change thresholds; define “new/risky device” (fingerprint, IP range, geo, OS version, jailbreak/root, emulator)
  - Tiered authentication: trigger step-up on new/anomalous login (2FA/MFA/face ID, etc.); step-up again for critical actions; define failure/degrade (allow only low-risk/low-limit or manual)
  - Sessions/concurrency: define multi-device concurrency policy; on key security events (rebind, password reset, strong-auth failures, repeated errors) rotate tokens, invalidate sessions, kick out, or freeze; define unfreeze and recovery
  - Notification/appeal: notify users and provide quick stop-loss entry (freeze/change password/support) for new device login/anomalous login/session kick/critical actions
  - Audit trail: log device ID, login method, triggered rule, risk score, actions taken, rule version and evidence
- Benefit: Reduce account theft and loss; improve controllability and auditability
- Applicability/strictness: Mainly for ToC. For internal ToB under managed endpoints/network, not mandatory unless external internet access or high-sensitivity operations exist.
- Strict recommendations (ToC): Default step-up for new/high-risk devices; restrict high-risk ops during cool-down; provide one-click freeze/kick-all/force re-login; freeze + manual review on repeated failures; require full traceability and notifications
- When to use: ToC login/multi-session/device change/geo anomalies/pre-auth for funds; ToB only when external access/strict compliance
- Related quality dimension: Security, Robustness, Traceability

#### 121. Missing industry applicability declaration causes cross-industry false positives/negatives
- Category: Rule governance gap
- Keywords: “industry/scope/cross-industry/false positive/false negative”
- Description: Not separating rules by industry (or missing “applicable industry” in rule files) causes bank/medical/e-commerce rules to be wrongly applied elsewhere, producing many false positives; or missing enabling correct industry rules causing false negatives.
- Bad example: Scanning bank reconciliation rules on e-commerce after-sales; scanning medical privacy rules on social apps; loading multiple industry rule files together
- Recommendation: Establish “industry declaration + rule assembly + fallback strategy”
  - Industry declaration: Each quality file must specify “Applicable industry: xxx” at top, optionally “not applicable/exception”; general rules should be marked “General (cross-industry)”
  - Rule assembly: Select rules by requirement’s industry: general + that industry; forbid cross-industry mixing
  - Fallback: When industry is unknown/unlabeled, enable only general rules by default; to enable industry rules, must explicitly specify industry or limit rule dir to a single industry
  - Governance: Group by industry in naming/dirs; industry rule changes require review; record enabled industry and rule version for audit and reproducibility
- Benefit: Lower false positives, reduce wasted communication, avoid missing key industry risks, improve governability and reproducibility
- When to use: Rule engines/quality scanning/multi-industry platforms
- Related quality dimension: Correctness, Maintainability

#### 122. Sensitive info display lacks masking/least-privilege, causing privacy/compliance risks
- Category: Security & privacy compliance gap
- Keywords: “masking/least privilege/sensitive info/customer support”
- Description: When requirements involve displaying sensitive information (contact, ID, address, account identifiers), missing role-based minimization and masking causes internal over-access, leakage and compliance risks. Especially for customer support, agents should not see full contact info; they should reach users via system capabilities (system calls/in-app messages).
- Bad example: Support UI shows full phone/email/ID/address; can copy-export all; “can view user info” without masking/permission/audit
- Recommendation: Define “data classification + masking + access control + auditing”
  - Data classification: Define sensitive/highly sensitive fields (phone/email/ID/address/bank card/account statements) with jurisdiction/industry handling rules
  - Masking: Default masking rules (phone 3-4-4, partial email hide, ID show first/last digits); full display requires strong conditions (authorization, ticket, dual control)
  - Least privilege by role: Role matrix defines field visibility for support/ops/reviewer/admin; sensitive fields default hidden or only reachable via “system call/system message”, forbid copying raw text
  - Operation constraints: Restrict export/copy, add screenshot watermark, rate-limit access; require reason code and approval chain for view/export/decrypt
  - Audit logs: Record view/decrypt/export/call actions (operator/time/object/reason code/ticket/result/IP/device)
- Benefit: Reduce leakage risk, meet privacy compliance and internal audit, improve permission governance feasibility
- When to use: Support systems, ops backends, review consoles, risk/fraud, external APIs and report exports
- Related quality dimension: Security, Compliance, Traceability
