/*
 * The author licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openhab.binding.spacetrack.internal.client.predicate;


import jdk.nashorn.internal.objects.annotations.Getter;
import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.spacetrack.internal.client.query.QueryField;

/**
 * A {@link Predicate} that filters results based on whether or not the given field is not null
 * 
 * @author Steven Paligo
 */

public class IsNotNull<T extends QueryField> implements Predicate<T> {

  private T field;


  public IsNotNull(@NonNull T field) {
    this.field = field;
  }


  public String toQueryParameter() {
    return field.getQueryFieldName() + "/<>null-val";
  }

  public T getField() {
    return field;
  }
}
