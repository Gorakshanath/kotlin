/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.cli.common.script

import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.script.ScriptError
import org.jetbrains.kotlin.script.ScriptErrorManager
import kotlin.script.dependencies.ScriptContents
import kotlin.script.dependencies.ScriptDependenciesResolver

class CliScriptErrorManager(private val messageCollector: MessageCollector) : ScriptErrorManager {
    override val lastErrors: List<ScriptError>
        get() = TODO("not implemented")

    override fun setErrors(scriptFile: VirtualFile, errors: List<ScriptError>) {
        errors.forEach {
            messageCollector.report(it.severity.convertSeverity(), it.message, location(scriptFile, it.position))
        }
    }

    private fun location(scriptFile: VirtualFile, position: ScriptContents.Position?): CompilerMessageLocation? {
        if (position == null) return CompilerMessageLocation.create(scriptFile.path)

        return CompilerMessageLocation.create(scriptFile.path, position.line, position.col, null)
    }

    private fun ScriptDependenciesResolver.ReportSeverity.convertSeverity(): CompilerMessageSeverity = when(this) {
        ScriptDependenciesResolver.ReportSeverity.ERROR -> CompilerMessageSeverity.ERROR
        ScriptDependenciesResolver.ReportSeverity.WARNING -> CompilerMessageSeverity.WARNING
        ScriptDependenciesResolver.ReportSeverity.INFO -> CompilerMessageSeverity.INFO
        ScriptDependenciesResolver.ReportSeverity.DEBUG -> CompilerMessageSeverity.LOGGING
    }
}

