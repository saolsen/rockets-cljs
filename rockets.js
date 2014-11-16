goog.addDependency("base.js", ['goog'], []);
goog.addDependency("../cljs/core.js", ['cljs.core'], ['goog.string', 'goog.object', 'goog.string.StringBuffer', 'goog.array']);
goog.addDependency("../om/dom.js", ['om.dom'], ['cljs.core']);
goog.addDependency("../om/core.js", ['om.core'], ['cljs.core', 'om.dom', 'goog.ui.IdGenerator']);
goog.addDependency("../rockets/scene.js", ['rockets.scene'], ['cljs.core', 'om.dom', 'om.core']);
goog.addDependency("../rockets/physics.js", ['rockets.physics'], ['cljs.core']);
goog.addDependency("../clojure/string.js", ['clojure.string'], ['goog.string', 'cljs.core', 'goog.string.StringBuffer']);
goog.addDependency("../rockets/core.js", ['rockets.core'], ['cljs.core', 'om.dom', 'rockets.physics', 'om.core', 'clojure.string', 'rockets.scene']);