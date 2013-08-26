package models.traits

import java.util.Date

trait Timestamped {
  def getCreateTime(): Date
  def getModifyTime(): Date
}

