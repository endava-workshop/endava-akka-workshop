package rest.api

import com.wordnik.swagger.annotations.ApiModelProperty
import scala.annotation.meta.field

// TODO incomplete - WIP
/**
 * REST API representation of ```entity.SimpleURL``` class
 * @author Ionuț Păduraru
 */
class SimpleUrlData(
  @(ApiModelProperty @field)(value = "The name of this simple URL") name: String,
  @(ApiModelProperty @field)(value = "The actual URL") url: String)