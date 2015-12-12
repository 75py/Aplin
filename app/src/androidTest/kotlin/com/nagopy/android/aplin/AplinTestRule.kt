/*
 * Copyright 2015 75py
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nagopy.android.aplin

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.*

class AplinTestRule : TestRule {

    var moreMessages = ArrayList<String>()

    fun setMessages(vararg messages: String) {
        moreMessages = messages.toArrayList()
    }

    fun addMessage(message: String) {
        moreMessages.add(message)
    }

    override fun apply(base: Statement, description: Description): Statement {
        return LoggerStatement(base, description, this)
    }

    class LoggerStatement(val base: Statement, val description: Description, val aplinTestRule: AplinTestRule) : Statement() {
        override fun evaluate() {
            try {
                base.evaluate()
            } catch(t: Throwable) {
                throw AssertionError(aplinTestRule.moreMessages.toString(), t)
            } finally {
                aplinTestRule.moreMessages.clear()
            }
        }

    }

}