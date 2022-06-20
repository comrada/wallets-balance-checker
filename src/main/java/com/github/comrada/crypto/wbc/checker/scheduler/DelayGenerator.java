/*
 * Copyright (c) 2022, powercloud GmbH and/or its affiliates. All rights reserved.
 * These programs are confidential and proprietary, and as such are protected by copyright law as unpublished works and
 * by international treaties to the fullest extent under the applicable law. Use is subject to license terms.
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.comrada.crypto.wbc.checker.scheduler;

import java.time.Duration;

public interface DelayGenerator {

  /**
   * @return next delay, presumably can change the internal state to generate the next delay.
   */
  Duration next();

  /**
   * @return next delay without changing the internal state.
   */
  Duration peekNext();

  /**
   * In case of a successful attempt, the generator may have an internal state reset.
   */
  default void reset() {

  }
}
