/*
 * Copyright 2018 Poly Forest, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File

rootProject.name = "acornui-binary-plugins"

/**
 * This if/else conditional is only to use and examine this template plugin in an actual project.
 * If this template is copied and used to create a production plugin, it's recommended to remove the conditional and
 * uncomment the line below.
 */
val separator = File.separator
val pluginsRoot = if (File(rootDir.parent).name == "templates")
	rootDir.canonicalPath.split(separator).dropLast(3).joinToString(separator)
else
	rootDir.canonicalPath.split(separator).dropLast(2).joinToString(separator)
//val pluginsRoot = rootDir.canonicalPath.split(separator).dropLast(2).joinToString(separator)
apply(from = "$pluginsRoot/scripts/plugins/shared-plugins.settings.gradle.kts")