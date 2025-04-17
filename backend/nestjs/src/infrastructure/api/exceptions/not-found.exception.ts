import { HttpException, HttpStatus } from '@nestjs/common';

export class NotFoundException extends HttpException {
  constructor(resource: string, id: string) {
    super(
      `${resource} with ID ${id} not found`,
      HttpStatus.NOT_FOUND,
    );
  }
} 