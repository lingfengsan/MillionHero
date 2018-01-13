# Copyright 2015 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

"""Provides a variety of device interactions with power.
"""
# pylint: disable=unused-argument

import collections
import contextlib
import csv
import logging

from devil.android import decorators
from devil.android import device_errors
from devil.android import device_utils
from devil.android.sdk import version_codes
from devil.utils import timeout_retry

logger = logging.getLogger(__name__)

_DEFAULT_TIMEOUT = 30
_DEFAULT_RETRIES = 3


_DEVICE_PROFILES = [
  {
    'name': ['Nexus 4'],
    'enable_command': (
        'echo 0 > /sys/module/pm8921_charger/parameters/disabled && '
        'dumpsys battery reset'),
    'disable_command': (
        'echo 1 > /sys/module/pm8921_charger/parameters/disabled && '
        'dumpsys battery set ac 0 && dumpsys battery set usb 0'),
    'charge_counter': None,
    'voltage': None,
    'current': None,
  },
  {
    'name': ['Nexus 5'],
    # Nexus 5
    # Setting the HIZ bit of the bq24192 causes the charger to actually ignore
    # energy coming from USB. Setting the power_supply offline just updates the
    # Android system to reflect that.
    'enable_command': (
        'echo 0x4A > /sys/kernel/debug/bq24192/INPUT_SRC_CONT && '
        'chmod 644 /sys/class/power_supply/usb/online && '
        'echo 1 > /sys/class/power_supply/usb/online && '
        'dumpsys battery reset'),
    'disable_command': (
        'echo 0xCA > /sys/kernel/debug/bq24192/INPUT_SRC_CONT && '
        'chmod 644 /sys/class/power_supply/usb/online && '
        'echo 0 > /sys/class/power_supply/usb/online && '
        'dumpsys battery set ac 0 && dumpsys battery set usb 0'),
    'charge_counter': None,
    'voltage': None,
    'current': None,
  },
  {
    'name': ['Nexus 6'],
    'enable_command': (
        'echo 1 > /sys/class/power_supply/battery/charging_enabled && '
        'dumpsys battery reset'),
    'disable_command': (
        'echo 0 > /sys/class/power_supply/battery/charging_enabled && '
        'dumpsys battery set ac 0 && dumpsys battery set usb 0'),
    'charge_counter': (
        '/sys/class/power_supply/max170xx_battery/charge_counter_ext'),
    'voltage': '/sys/class/power_supply/max170xx_battery/voltage_now',
    'current': '/sys/class/power_supply/max170xx_battery/current_now',
  },
  {
    'name': ['Nexus 9'],
    'enable_command': (
        'echo Disconnected > '
        '/sys/bus/i2c/drivers/bq2419x/0-006b/input_cable_state && '
        'dumpsys battery reset'),
    'disable_command': (
        'echo Connected > '
        '/sys/bus/i2c/drivers/bq2419x/0-006b/input_cable_state && '
        'dumpsys battery set ac 0 && dumpsys battery set usb 0'),
    'charge_counter': '/sys/class/power_supply/battery/charge_counter_ext',
    'voltage': '/sys/class/power_supply/battery/voltage_now',
    'current': '/sys/class/power_supply/battery/current_now',
  },
  {
    'name': ['Nexus 10'],
    'enable_command': None,
    'disable_command': None,
    'charge_counter': None,
    'voltage': '/sys/class/power_supply/ds2784-fuelgauge/voltage_now',
    'current': '/sys/class/power_supply/ds2784-fuelgauge/current_now',

  },
  {
    'name': ['Nexus 5X'],
    'enable_command': (
        'echo 1 > /sys/class/power_supply/battery/charging_enabled && '
        'dumpsys battery reset'),
    'disable_command': (
        'echo 0 > /sys/class/power_supply/battery/charging_enabled && '
        'dumpsys battery set ac 0 && dumpsys battery set usb 0'),
    'charge_counter': None,
    'voltage': None,
    'current': None,
  },
  { # Galaxy s5
    'name': ['SM-G900H'],
    'enable_command': (
        'chmod 644 /sys/class/power_supply/battery/test_mode && '
        'chmod 644 /sys/class/power_supply/sec-charger/current_now && '
        'echo 0 > /sys/class/power_supply/battery/test_mode && '
        'echo 9999 > /sys/class/power_supply/sec-charger/current_now &&'
        'dumpsys battery reset'),
    'disable_command': (
        'chmod 644 /sys/class/power_supply/battery/test_mode && '
        'chmod 644 /sys/class/power_supply/sec-charger/current_now && '
        'echo 1 > /sys/class/power_supply/battery/test_mode && '
        'echo 0 > /sys/class/power_supply/sec-charger/current_now && '
        'dumpsys battery set ac 0 && dumpsys battery set usb 0'),
    'charge_counter': None,
    'voltage': '/sys/class/power_supply/sec-fuelgauge/voltage_now',
    'current': '/sys/class/power_supply/sec-charger/current_now',
  },
  { # Galaxy s6, Galaxy s6, Galaxy s6 edge
    'name': ['SM-G920F', 'SM-G920V', 'SM-G925V'],
    'enable_command': (
        'chmod 644 /sys/class/power_supply/battery/test_mode && '
        'chmod 644 /sys/class/power_supply/max77843-charger/current_now && '
        'echo 0 > /sys/class/power_supply/battery/test_mode && '
        'echo 9999 > /sys/class/power_supply/max77843-charger/current_now &&'
        'dumpsys battery reset'),
    'disable_command': (
        'chmod 644 /sys/class/power_supply/battery/test_mode && '
        'chmod 644 /sys/class/power_supply/max77843-charger/current_now && '
        'echo 1 > /sys/class/power_supply/battery/test_mode && '
        'echo 0 > /sys/class/power_supply/max77843-charger/current_now && '
        'dumpsys battery set ac 0 && dumpsys battery set usb 0'),
    'charge_counter': None,
    'voltage': '/sys/class/power_supply/max77843-fuelgauge/voltage_now',
    'current': '/sys/class/power_supply/max77843-charger/current_now',
  },
  { # Cherry Mobile One
    'name': ['W6210 (4560MMX_b fingerprint)'],
    'enable_command': (
        'echo "0 0" > /proc/mtk_battery_cmd/current_cmd && '
        'dumpsys battery reset'),
    'disable_command': (
        'echo "0 1" > /proc/mtk_battery_cmd/current_cmd && '
        'dumpsys battery set ac 0 && dumpsys battery set usb 0'),
    'charge_counter': None,
    'voltage': None,
    'current': None,
},
]

# The list of useful dumpsys columns.
# Index of the column containing the format version.
_DUMP_VERSION_INDEX = 0
# Index of the column containing the type of the row.
_ROW_TYPE_INDEX = 3
# Index of the column containing the uid.
_PACKAGE_UID_INDEX = 4
# Index of the column containing the application package.
_PACKAGE_NAME_INDEX = 5
# The column containing the uid of the power data.
_PWI_UID_INDEX = 1
# The column containing the type of consumption. Only consumption since last
# charge are of interest here.
_PWI_AGGREGATION_INDEX = 2
_PWS_AGGREGATION_INDEX = _PWI_AGGREGATION_INDEX
# The column containing the amount of power used, in mah.
_PWI_POWER_CONSUMPTION_INDEX = 5
_PWS_POWER_CONSUMPTION_INDEX = _PWI_POWER_CONSUMPTION_INDEX

_MAX_CHARGE_ERROR = 20


class BatteryUtils(object):

  def __init__(self, device, default_timeout=_DEFAULT_TIMEOUT,
               default_retries=_DEFAULT_RETRIES):
    """BatteryUtils constructor.

      Args:
        device: A DeviceUtils instance.
        default_timeout: An integer containing the default number of seconds to
                         wait for an operation to complete if no explicit value
                         is provided.
        default_retries: An integer containing the default number or times an
                         operation should be retried on failure if no explicit
                         value is provided.
      Raises:
        TypeError: If it is not passed a DeviceUtils instance.
    """
    if not isinstance(device, device_utils.DeviceUtils):
      raise TypeError('Must be initialized with DeviceUtils object.')
    self._device = device
    self._cache = device.GetClientCache(self.__class__.__name__)
    self._default_timeout = default_timeout
    self._default_retries = default_retries

  @decorators.WithTimeoutAndRetriesFromInstance()
  def SupportsFuelGauge(self, timeout=None, retries=None):
    """Detect if fuel gauge chip is present.

    Args:
      timeout: timeout in seconds
      retries: number of retries

    Returns:
      True if known fuel gauge files are present.
      False otherwise.
    """
    self._DiscoverDeviceProfile()
    return (self._cache['profile']['enable_command'] != None
        and self._cache['profile']['charge_counter'] != None)

  @decorators.WithTimeoutAndRetriesFromInstance()
  def GetFuelGaugeChargeCounter(self, timeout=None, retries=None):
    """Get value of charge_counter on fuel gauge chip.

    Device must have charging disabled for this, not just battery updates
    disabled. The only device that this currently works with is the nexus 5.

    Args:
      timeout: timeout in seconds
      retries: number of retries

    Returns:
      value of charge_counter for fuel gauge chip in units of nAh.

    Raises:
      device_errors.CommandFailedError: If fuel gauge chip not found.
    """
    if self.SupportsFuelGauge():
      return int(self._device.ReadFile(
          self._cache['profile']['charge_counter']))
    raise device_errors.CommandFailedError(
        'Unable to find fuel gauge.')

  @decorators.WithTimeoutAndRetriesFromInstance()
  def GetNetworkData(self, package, timeout=None, retries=None):
    """Get network data for specific package.

    Args:
      package: package name you want network data for.
      timeout: timeout in seconds
      retries: number of retries

    Returns:
      Tuple of (sent_data, recieved_data)
      None if no network data found
    """
    # If device_utils clears cache, cache['uids'] doesn't exist
    if 'uids' not in self._cache:
      self._cache['uids'] = {}
    if package not in self._cache['uids']:
      self.GetPowerData()
      if package not in self._cache['uids']:
        logger.warning('No UID found for %s. Can\'t get network data.',
                       package)
        return None

    network_data_path = '/proc/uid_stat/%s/' % self._cache['uids'][package]
    try:
      send_data = int(self._device.ReadFile(network_data_path + 'tcp_snd'))
    # If ReadFile throws exception, it means no network data usage file for
    # package has been recorded. Return 0 sent and 0 received.
    except device_errors.AdbShellCommandFailedError:
      logger.warning('No sent data found for package %s', package)
      send_data = 0
    try:
      recv_data = int(self._device.ReadFile(network_data_path + 'tcp_rcv'))
    except device_errors.AdbShellCommandFailedError:
      logger.warning('No received data found for package %s', package)
      recv_data = 0
    return (send_data, recv_data)

  @decorators.WithTimeoutAndRetriesFromInstance()
  def GetPowerData(self, timeout=None, retries=None):
    """Get power data for device.

    Args:
      timeout: timeout in seconds
      retries: number of retries

    Returns:
      Dict containing system power, and a per-package power dict keyed on
      package names.
      {
        'system_total': 23.1,
        'per_package' : {
          package_name: {
            'uid': uid,
            'data': [1,2,3]
          },
        }
      }
    """
    if 'uids' not in self._cache:
      self._cache['uids'] = {}
    dumpsys_output = self._device.RunShellCommand(
        ['dumpsys', 'batterystats', '-c'],
        check_return=True, large_output=True)
    csvreader = csv.reader(dumpsys_output)
    pwi_entries = collections.defaultdict(list)
    system_total = None
    for entry in csvreader:
      if entry[_DUMP_VERSION_INDEX] not in ['8', '9']:
        # Wrong dumpsys version.
        raise device_errors.DeviceVersionError(
            'Dumpsys version must be 8 or 9. "%s" found.'
            % entry[_DUMP_VERSION_INDEX])
      if _ROW_TYPE_INDEX < len(entry) and entry[_ROW_TYPE_INDEX] == 'uid':
        current_package = entry[_PACKAGE_NAME_INDEX]
        if (self._cache['uids'].get(current_package)
            and self._cache['uids'].get(current_package)
            != entry[_PACKAGE_UID_INDEX]):
          raise device_errors.CommandFailedError(
              'Package %s found multiple times with different UIDs %s and %s'
               % (current_package, self._cache['uids'][current_package],
               entry[_PACKAGE_UID_INDEX]))
        self._cache['uids'][current_package] = entry[_PACKAGE_UID_INDEX]
      elif (_PWI_POWER_CONSUMPTION_INDEX < len(entry)
          and entry[_ROW_TYPE_INDEX] == 'pwi'
          and entry[_PWI_AGGREGATION_INDEX] == 'l'):
        pwi_entries[entry[_PWI_UID_INDEX]].append(
            float(entry[_PWI_POWER_CONSUMPTION_INDEX]))
      elif (_PWS_POWER_CONSUMPTION_INDEX < len(entry)
          and entry[_ROW_TYPE_INDEX] == 'pws'
          and entry[_PWS_AGGREGATION_INDEX] == 'l'):
        # This entry should only appear once.
        assert system_total is None
        system_total = float(entry[_PWS_POWER_CONSUMPTION_INDEX])

    per_package = {p: {'uid': uid, 'data': pwi_entries[uid]}
                   for p, uid in self._cache['uids'].iteritems()}
    return {'system_total': system_total, 'per_package': per_package}

  @decorators.WithTimeoutAndRetriesFromInstance()
  def GetBatteryInfo(self, timeout=None, retries=None):
    """Gets battery info for the device.

    Args:
      timeout: timeout in seconds
      retries: number of retries
    Returns:
      A dict containing various battery information as reported by dumpsys
      battery.
    """
    result = {}
    # Skip the first line, which is just a header.
    for line in self._device.RunShellCommand(
        ['dumpsys', 'battery'], check_return=True)[1:]:
      # If usb charging has been disabled, an extra line of header exists.
      if 'UPDATES STOPPED' in line:
        logger.warning('Dumpsys battery not receiving updates. '
                       'Run dumpsys battery reset if this is in error.')
      elif ':' not in line:
        logger.warning('Unknown line found in dumpsys battery: "%s"', line)
      else:
        k, v = line.split(':', 1)
        result[k.strip()] = v.strip()
    return result

  @decorators.WithTimeoutAndRetriesFromInstance()
  def GetCharging(self, timeout=None, retries=None):
    """Gets the charging state of the device.

    Args:
      timeout: timeout in seconds
      retries: number of retries
    Returns:
      True if the device is charging, false otherwise.
    """
    battery_info = self.GetBatteryInfo()
    for k in ('AC powered', 'USB powered', 'Wireless powered'):
      if (k in battery_info and
          battery_info[k].lower() in ('true', '1', 'yes')):
        return True
    return False

  # TODO(rnephew): Make private when all use cases can use the context manager.
  @decorators.WithTimeoutAndRetriesFromInstance()
  def DisableBatteryUpdates(self, timeout=None, retries=None):
    """Resets battery data and makes device appear like it is not
    charging so that it will collect power data since last charge.

    Args:
      timeout: timeout in seconds
      retries: number of retries

    Raises:
      device_errors.CommandFailedError: When resetting batterystats fails to
        reset power values.
      device_errors.DeviceVersionError: If device is not L or higher.
    """
    def battery_updates_disabled():
      return self.GetCharging() is False

    self._ClearPowerData()
    self._device.RunShellCommand(['dumpsys', 'battery', 'set', 'ac', '0'],
                                 check_return=True)
    self._device.RunShellCommand(['dumpsys', 'battery', 'set', 'usb', '0'],
                                 check_return=True)
    timeout_retry.WaitFor(battery_updates_disabled, wait_period=1)

  # TODO(rnephew): Make private when all use cases can use the context manager.
  @decorators.WithTimeoutAndRetriesFromInstance()
  def EnableBatteryUpdates(self, timeout=None, retries=None):
    """Restarts device charging so that dumpsys no longer collects power data.

    Args:
      timeout: timeout in seconds
      retries: number of retries

    Raises:
      device_errors.DeviceVersionError: If device is not L or higher.
    """
    def battery_updates_enabled():
      return (self.GetCharging()
              or not bool('UPDATES STOPPED' in self._device.RunShellCommand(
                  ['dumpsys', 'battery'], check_return=True)))

    self._device.RunShellCommand(['dumpsys', 'battery', 'reset'],
                                 check_return=True)
    timeout_retry.WaitFor(battery_updates_enabled, wait_period=1)

  @contextlib.contextmanager
  def BatteryMeasurement(self, timeout=None, retries=None):
    """Context manager that enables battery data collection. It makes
    the device appear to stop charging so that dumpsys will start collecting
    power data since last charge. Once the with block is exited, charging is
    resumed and power data since last charge is no longer collected.

    Only for devices L and higher.

    Example usage:
      with BatteryMeasurement():
        browser_actions()
        get_power_data() # report usage within this block
      after_measurements() # Anything that runs after power
                           # measurements are collected

    Args:
      timeout: timeout in seconds
      retries: number of retries

    Raises:
      device_errors.DeviceVersionError: If device is not L or higher.
    """
    if self._device.build_version_sdk < version_codes.LOLLIPOP:
      raise device_errors.DeviceVersionError('Device must be L or higher.')
    try:
      self.DisableBatteryUpdates(timeout=timeout, retries=retries)
      yield
    finally:
      self.EnableBatteryUpdates(timeout=timeout, retries=retries)

  def _DischargeDevice(self, percent, wait_period=120):
    """Disables charging and waits for device to discharge given amount

    Args:
      percent: level of charge to discharge.

    Raises:
      ValueError: If percent is not between 1 and 99.
    """
    battery_level = int(self.GetBatteryInfo().get('level'))
    if not 0 < percent < 100:
      raise ValueError('Discharge amount(%s) must be between 1 and 99'
                       % percent)
    if battery_level is None:
      logger.warning('Unable to find current battery level. Cannot discharge.')
      return
    # Do not discharge if it would make battery level too low.
    if percent >= battery_level - 10:
      logger.warning('Battery is too low or discharge amount requested is too '
                     'high. Cannot discharge phone %s percent.', percent)
      return

    self._HardwareSetCharging(False)

    def device_discharged():
      self._HardwareSetCharging(True)
      current_level = int(self.GetBatteryInfo().get('level'))
      logger.info('current battery level: %s', current_level)
      if battery_level - current_level >= percent:
        return True
      self._HardwareSetCharging(False)
      return False

    timeout_retry.WaitFor(device_discharged, wait_period=wait_period)

  def ChargeDeviceToLevel(self, level, wait_period=60):
    """Enables charging and waits for device to be charged to given level.

    Args:
      level: level of charge to wait for.
      wait_period: time in seconds to wait between checking.
    Raises:
      device_errors.DeviceChargingError: If error while charging is detected.
    """
    self.SetCharging(True)
    charge_status = {
        'charge_failure_count': 0,
        'last_charge_value': 0
    }
    def device_charged():
      battery_level = self.GetBatteryInfo().get('level')
      if battery_level is None:
        logger.warning('Unable to find current battery level.')
        battery_level = 100
      else:
        logger.info('current battery level: %s', battery_level)
        battery_level = int(battery_level)

      # Use > so that it will not reset if charge is going down.
      if battery_level > charge_status['last_charge_value']:
        charge_status['last_charge_value'] = battery_level
        charge_status['charge_failure_count'] = 0
      else:
        charge_status['charge_failure_count'] += 1

      if (not battery_level >= level
          and charge_status['charge_failure_count'] >= _MAX_CHARGE_ERROR):
        raise device_errors.DeviceChargingError(
            'Device not charging properly. Current level:%s Previous level:%s'
             % (battery_level, charge_status['last_charge_value']))
      return battery_level >= level

    timeout_retry.WaitFor(device_charged, wait_period=wait_period)

  def LetBatteryCoolToTemperature(self, target_temp, wait_period=180):
    """Lets device sit to give battery time to cool down
    Args:
      temp: maximum temperature to allow in tenths of degrees c.
      wait_period: time in seconds to wait between checking.
    """
    def cool_device():
      temp = self.GetBatteryInfo().get('temperature')
      if temp is None:
        logger.warning('Unable to find current battery temperature.')
        temp = 0
      else:
        logger.info('Current battery temperature: %s', temp)
      if int(temp) <= target_temp:
        return True
      else:
        if 'Nexus 5' in self._cache['profile']['name']:
          self._DischargeDevice(1)
        return False

    self._DiscoverDeviceProfile()
    self.EnableBatteryUpdates()
    logger.info('Waiting for the device to cool down to %s (0.1 C)',
                target_temp)
    timeout_retry.WaitFor(cool_device, wait_period=wait_period)

  @decorators.WithTimeoutAndRetriesFromInstance()
  def SetCharging(self, enabled, timeout=None, retries=None):
    """Enables or disables charging on the device.

    Args:
      enabled: A boolean indicating whether charging should be enabled or
        disabled.
      timeout: timeout in seconds
      retries: number of retries
    """
    if self.GetCharging() == enabled:
      logger.warning('Device charging already in expected state: %s', enabled)
      return

    self._DiscoverDeviceProfile()
    if enabled:
      if self._cache['profile']['enable_command']:
        self._HardwareSetCharging(enabled)
      else:
        logger.info('Unable to enable charging via hardware. '
                    'Falling back to software enabling.')
        self.EnableBatteryUpdates()
    else:
      if self._cache['profile']['enable_command']:
        self._ClearPowerData()
        self._HardwareSetCharging(enabled)
      else:
        logger.info('Unable to disable charging via hardware. '
                     'Falling back to software disabling.')
        self.DisableBatteryUpdates()

  def _HardwareSetCharging(self, enabled, timeout=None, retries=None):
    """Enables or disables charging on the device.

    Args:
      enabled: A boolean indicating whether charging should be enabled or
        disabled.
      timeout: timeout in seconds
      retries: number of retries

    Raises:
      device_errors.CommandFailedError: If method of disabling charging cannot
        be determined.
    """
    self._DiscoverDeviceProfile()
    if not self._cache['profile']['enable_command']:
      raise device_errors.CommandFailedError(
          'Unable to find charging commands.')

    command = (self._cache['profile']['enable_command'] if enabled
               else self._cache['profile']['disable_command'])

    def verify_charging():
      return self.GetCharging() == enabled

    self._device.RunShellCommand(
        command, check_return=True, as_root=True, large_output=True)
    timeout_retry.WaitFor(verify_charging, wait_period=1)

  @contextlib.contextmanager
  def PowerMeasurement(self, timeout=None, retries=None):
    """Context manager that enables battery power collection.

    Once the with block is exited, charging is resumed. Will attempt to disable
    charging at the hardware level, and if that fails will fall back to software
    disabling of battery updates.

    Only for devices L and higher.

    Example usage:
      with PowerMeasurement():
        browser_actions()
        get_power_data() # report usage within this block
      after_measurements() # Anything that runs after power
                           # measurements are collected

    Args:
      timeout: timeout in seconds
      retries: number of retries
    """
    try:
      self.SetCharging(False, timeout=timeout, retries=retries)
      yield
    finally:
      self.SetCharging(True, timeout=timeout, retries=retries)

  def _ClearPowerData(self):
    """Resets battery data and makes device appear like it is not
    charging so that it will collect power data since last charge.

    Returns:
      True if power data cleared.
      False if power data clearing is not supported (pre-L)

    Raises:
      device_errors.DeviceVersionError: If power clearing is supported,
        but fails.
    """
    if self._device.build_version_sdk < version_codes.LOLLIPOP:
      logger.warning('Dumpsys power data only available on 5.0 and above. '
                     'Cannot clear power data.')
      return False

    self._device.RunShellCommand(
        ['dumpsys', 'battery', 'set', 'usb', '1'], check_return=True)
    self._device.RunShellCommand(
        ['dumpsys', 'battery', 'set', 'ac', '1'], check_return=True)

    def test_if_clear():
      self._device.RunShellCommand(
          ['dumpsys', 'batterystats', '--reset'], check_return=True)
      battery_data = self._device.RunShellCommand(
          ['dumpsys', 'batterystats', '--charged', '-c'],
          check_return=True, large_output=True)
      for line in battery_data:
        l = line.split(',')
        if (len(l) > _PWI_POWER_CONSUMPTION_INDEX
            and l[_ROW_TYPE_INDEX] == 'pwi'
            and float(l[_PWI_POWER_CONSUMPTION_INDEX]) != 0.0):
          return False
      return True

    try:
      timeout_retry.WaitFor(test_if_clear, wait_period=1)
      return True
    finally:
      self._device.RunShellCommand(
          ['dumpsys', 'battery', 'reset'], check_return=True)

  def _DiscoverDeviceProfile(self):
    """Checks and caches device information.

    Returns:
      True if profile is found, false otherwise.
    """

    if 'profile' in self._cache:
      return True
    for profile in _DEVICE_PROFILES:
      if self._device.product_model in profile['name']:
        self._cache['profile'] = profile
        return True
    self._cache['profile'] = {
        'name': [],
        'enable_command': None,
        'disable_command': None,
        'charge_counter': None,
        'voltage': None,
        'current': None,
    }
    return False
